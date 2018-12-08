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
    - git checkout <name>-dev
    - git merge all-dev


DO NOT COMMIT TO MASTER OR EVEN THINK ABOUT MASTER. AT THE END OF ALL SPRINTS WE WILL MERGE TO MASTER
