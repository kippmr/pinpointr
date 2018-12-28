# Pinpointr
2018 capstone project, goal is to pinpoint issues on a campus with nothing received but a photo

### Link to Trello:
https://trello.com/b/cIDZks4y/pinpointr

### Link to TensorFlow website:
https://www.tensorflow.org/api_docs/

# Sample Workflow
- Use android app for object recognition
- Send to .NET Core backend to save on database
- React frontend recieves data via API calls from backend
- frontend displays Leaflet.js map with ArcGIS

# How to commit
Your workflow should look like the following: 
- Switch to your dev-branch 
- Code a bunch of stuff 
- Add for tracking: 
- git add * 
- git commit -m "commit message" 
- git push 
- pull request to merge into all-dev (this can be done on the github webpage, a link should be provided for you after you push)

How do I get my branch up to speed when all-dev changes? 
- git checkout all-dev 
- git pull 
- git checkout 
- dev 
- git merge all-dev

DO NOT COMMIT TO MASTER OR EVEN THINK ABOUT MASTER. AT THE END OF ALL SPRINTS WE WILL MERGE TO MASTER
