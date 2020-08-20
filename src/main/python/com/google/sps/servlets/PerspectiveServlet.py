from flask import Flask
from google.cloud import datastore
from googleapiclient import discovery
import requests
import json

app = Flask(__name__)
datastore = datastore.Client()


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
    attribute = "attributeScores"
    toxicity = "TOXICITY"
    summary = "summaryScore"
    val = "value"
    return result[attribute][toxicity][summary][val]


def fetch_rating(term_key, user_id):
    query_rating = datastore.query(kind='Rating', ancestor=term_key)
    query_rating.add_filter('reviewer-id', '=', user_id)
    query_rating_list = list(query_rating.fetch())

    return query_rating_list


@app.route('/perspective')
def root():
    # First, get ancestor and user ID from request.
    response = requests.get(url='/data')
    response_dict = json.loads(response.text)
    user_id = response_dict['ID']
    term_key = response_dict['termKey']
    # Now, fetch term rating.
    rating_entity = fetch_rating(term_key, user_id)
    # Get comments of terms and professors.
    term_feedback = rating_entity['comments-term']
    professor_feedback = rating_entity['comments-professor']
    # Calculate toxiticity score.
    toxicity_term_comment = commentAnalyzer(term_feedback)
    toxicity_professor_comment = commentAnalyzer(professor_feedback)
    # Send back to /data Servlet.
    toxicity_scores = {
        'toxicity_term_comment':
        toxicity_term_comment,
        'toxicity_professor_comment':
        toxicity_professor_comment}
    requests.post(url='/data', data=toxicity_scores)
    return


if __name__ == '__main__':
    # This is used when running locally only. When deploying to Google App
    # Engine, a webserver process such as Gunicorn will serve the app. This
    # can be configured by adding an `entrypoint` to app.yaml.
    # Flask's development server will automatically serve static files in
    # the "static" directory. See:
    # http://flask.pocoo.org/docs/1.0/quickstart/#static-files. Once deployed,
    # App Engine itself will serve those files as configured in app.yaml.
    app.run(host='127.0.0.1', port=8080, debug=True)
