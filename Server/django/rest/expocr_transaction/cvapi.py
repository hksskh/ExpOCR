import time
import requests
import json
import re

_url = 'https://westus.api.cognitive.microsoft.com/vision/v1.0/ocr'
_key = '8ecf5dc0d2e443c2a4b8db3072b17036'
_maxNumRetries = 10


class CVAPI:
    @staticmethod
    def processRequest(json, data, headers, params):
        """
            Helper function to process the request to Project Oxford

            Parameters:
            json: Used when processing images from its URL. See API Documentation
            data: Used when processing image read from disk. See API Documentation
            headers: Used to pass the key information and the data type request
            """

        retries = 0
        result = None

        while True:

            response = requests.request('post', _url, json=json, data=data, headers=headers, params=params)

            if response.status_code == 429:

                print("Message: %s" % (response.json()['error']['message']))

                if retries <= _maxNumRetries:
                    time.sleep(1)
                    retries += 1
                    continue
                else:
                    print('Error: failed after retrying!')
                    break

            elif response.status_code == 200 or response.status_code == 201:

                if 'content-length' in response.headers and int(response.headers['content-length']) == 0:
                    result = None
                elif 'content-type' in response.headers and isinstance(response.headers['content-type'], str):
                    if 'application/json' in response.headers['content-type'].lower():
                        result = response.json() if response.content else None
                    elif 'image' in response.headers['content-type'].lower():
                        result = response.content
            else:
                print("Error code: %d" % (response.status_code))
                print("Message: %s" % (response.json()['error']['message']))

            break

        return result

    @staticmethod
    def send_image_on_disk(data):
        params = {'language': 'en', 'detectOrientation': 'true'}

        headers = dict()
        headers['ocp-apim-subscription-key'] = _key
        headers['Content-Type'] = 'application/octet-stream'

        json = None

        result = CVAPI.processRequest(json, data, headers, params)

        if result is not None:
            print('success in ocr')

        return result

    @staticmethod
    def restore_receipt(data):

        boxTuples = []
        lineTuples = []

        # collect all lines with boundBox and words in BoxTuple object
        line_num = 0
        for region in data['regions']:
            parent_coordinates = region['boundingBox'].split(',')  # crucial
            for line in region['lines']:
                boxTuples.append(BoxTuple(list(parent_coordinates), line['boundingBox'].split(','), ''))
                tempString = []
                for word in line['words']:
                    tempString.append(word['text'] + ' ')
                boxTuples[line_num].text = ''.join(tempString)
                line_num += 1

        # sort BoxTuples by start y_axis
        boxTuples = sorted(boxTuples, BoxTuple.y_axis_sort)
        # group BoxTuples by y_axis range
        # multiple algorithms, chosen with probability stored in server file
        left = -1
        right = -1
        line_num = -1
        for bt in boxTuples:
            begin = int(bt.coordinates[1])
            end = begin + int(bt.coordinates[3])
            if left <= begin <= right:  # and (right - begin) > (begin - left):
                lineTuples[line_num].append(bt)
                # left = begin if begin < left else left
                # right = end if end > right else right
            else:
                line_num += 1
                lineTuples.append([])
                lineTuples[line_num].append(bt)
                left = begin
                right = end

        # handle BoxTuples in each group
        line_num = 0
        jsonArray = []
        for lt in lineTuples:
            lt = sorted(lt, BoxTuple.x_axis_sort)
            jsonArray.append({})
            tempLine = []
            for bt in lt:
                tempLine.append(bt.text)
                tempLine.append(', ')
            jsonArray[line_num]['text'] = ''.join(tempLine)
            jsonArray[line_num]['possible_price'] = [0]
            word_num = 1
            rawLine = []
            if len(lt) > 0:
                rawLine.append(lt[0].text.replace(' ', ''))
            while word_num < len(lt):
                # judge if two words should be merged, multiple algorithms, chosen from server file
                if not BoxTuple.has_same_parent(lt[word_num], lt[word_num - 1]) \
                        and not BoxTuple.is_x_axis_near(lt[word_num - 1], lt[word_num]):
                    rawLine.append(' ')
                rawLine.append(lt[word_num].text.replace(' ', ''))
                word_num += 1  # do not forget
            # OCR may mistakenly recognize . as , in float number
            rawLine = ''.join(rawLine).replace(',', '.')
            for price in re.findall(r'\d*\.\d+', rawLine):
                jsonArray[line_num]['possible_price'].append(float(price))
            jsonArray[line_num]['possible_price'].reverse()
            line_num += 1  # do not forget

        return jsonArray


# each BoxTuple represents a line (words)
class BoxTuple:
    # boundBox of parent region
    parent_coordinates = []
    # boundBox of this line
    coordinates = []
    # concatenation of words in this line
    text = ''

    def __init__(self, parent_coordinates, coordinates, text):
        self.parent_coordinates = parent_coordinates
        self.coordinates = coordinates
        self.text = text

    # compare two BoxTuples according to y axis in boundingBox
    @staticmethod
    def y_axis_sort(x, y):
        cmp_x = int(x.coordinates[1])
        cmp_y = int(y.coordinates[1])
        if cmp_x > cmp_y:
            return 1
        if cmp_x < cmp_y:
            return -1
        return 0

    # compare two BoxTuples according to x axis in boundingBox
    @staticmethod
    def x_axis_sort(x, y):
        cmp_x = int(x.coordinates[0])
        cmp_y = int(y.coordinates[0])
        if cmp_x > cmp_y:
            return 1
        if cmp_x < cmp_y:
            return -1
        return 0

    # judge if two BoxTuples has the same parent region
    @staticmethod
    def has_same_parent(x, y):
        if x.parent_coordinates == y.parent_coordinates:
            return True
        return False

    # judge if two BoxTuples are near in x axis
    @staticmethod
    def is_x_axis_near(x, y):
        if int(y.coordinates[0]) - (int(x.coordinates[0]) + int(x.coordinates[2])) < int(y.coordinates[2]):
            return True
        return False
