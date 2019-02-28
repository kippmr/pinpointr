function getToken() {
    return $.post("https://www.arcgis.com/sharing/rest/oauth2/token/", {
        "client_id": "4zKEN5BilxUnVaqy",
        "client_secret": "804e33b62c6247e4b2465d6cbc929e43",
        "grant_type": "client_credentials"
    });
}

