// README
CMPT 276 - COBALT

Some information and instructions:

1. Regarding API:
    - As opposed to the previous iteration, we now have support for multiple APIs
    - Our app has been tested rigorously on API 26 so we'd prefer for our Customer (TA/Prof) to
        test it on API 26
    - App is fully functional for APIs 24 and 29 as well but not as optimized and tested

2. Search Feature:
    - Since the description for the search/filter required multiple functionality, we have created
        a separate activity for searching.
    - Icons for searching are located on top right corners of both Map and List views

3. Internationalize:
    - Supported languages according to Canada: English, French
    - According to the professor's advice, we weren't supposed to translate data from CSV file,
        therefore, our violation descriptions are not translated since they have not been separately
        processed

4. Downloading:
    - To stop downloading from server, simply click anywhere on the screen area when you see the
        dialog box

5. Favourites:
    - Favourites are stored in json format using sharedPreferences
    - MapActivity has an option to access all the favourite items by clicking on the STAR icon
    - If the recent update makes any changes to favourite restaurants, they are shown before the
        map is launched. Once the user acknowledges the update, map is launched