# Pinpointr
Pinpointr is an app that allows anyone on campus to document an issue on campus with a single photo and a button press. The app uses machine learning technology and geolocation to figure out where and what the issue is. From there, it sends an alert to facilities services, so they can create a work order to deal with the issue. It also includes the ability to scan QR codes placed on facilities around campus, in order to report issues with specific trash cans, water fountains, etc

### Link to Trello:
https://trello.com/b/cIDZks4y/pinpointr

## Contents
1. <a href="#install">Installation</a>
1. <a href="#usage">Usage</a>
1. <a href="#commit">How to Commit</a>
1. <a href="#credit">Credits</a>

<a name="install" />

## Installation

_Visit the [wiki](https://github.com/kippmr/pinpointr/wiki) for more details_

<a name="usage" />

## Usage
- Use android app for object recognition
- Send to .NET Core backend to save on database
- React frontend recieves data via API calls from backend
- frontend displays Leaflet.js map with ArcGIS

<a name="commit" />

## How to Commit
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
- git checkout [YOUR BRANCH NAME]
- git merge all-dev

_AT THE END OF ALL SPRINTS WE WILL MERGE TO MASTER_

<a name="credit" />

## Credits
- Matthew Kipp
- Sean McKay
- Brandon Ronald
- Victor Timpau


_Contact @kippmr for appsettings.json and AWS credentials_
