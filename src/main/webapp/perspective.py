from googleapiclient import discovery
import json

text = "I hate you"
# text = sys.argv[0]


def commentAnalyzer(text):
    API_KEY = 'AIzaSyBnjF0OVUD3BGiuYFMSVe1_g134AKz3xQY'

# Generates API client object dynamically based on service name and version.
    service = discovery.build('commentanalyzer',
                              'v1alpha1', developerKey=API_KEY)

    analyze_request = {
        'comment':
        {'text': text},
        'requestedAttributes':
        {'TOXICITY': {}}}

    response = service.comments().analyze(body=analyze_request).execute()
    result = json.dumps(response, indent=2)
    result = json.loads(result)
    attribute = "attributeScores"
    toxicity = "TOXICITY"
    summary = "summaryScore"
    val = "value"
    return result[attribute][toxicity][summary][val]


print(commentAnalyzer(text))
