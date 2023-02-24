Music Advisor is a a personal music advisor that makes preference-based suggestions and even shares links to new releases and featured playlists. This is done by interacting via command-line with Spotify's API (Application Programming Interface).

<img src="https://user-images.githubusercontent.com/81112344/221165670-5aa78633-f8a7-4299-bee8-1445b315b08b.jpg" width="50%" align="left" />

Based on a Hyperskill project (https://hyperskill.org/projects/62), this application was developed in Java and integrates the major features offered by the API. The API itself is structured on simple REST (Representational State Transfer) principles, commonly used for communication with web services. 

As to the program, it is organized in terms of the MVC (Model-View-Controller), an architectural pattern for building an application. based on its separation in thre different parts in order to achieve a greater level of scalability and maintainability.

List of command options available: 

- auth - in order to user the application, the user must authenticate his/her Spotify account. The open authorization protocol OAuth2 is here used for that.  

- featured — obtain a list of links to all Spotify featured playlists;

- new — obtain a list of links to new albums with artists and links on Spotify;

- categories —  obtain a list of all available categories on Spotify (just their names);

- playlists C_NAME, where C_NAME — name of category. Obtain a list of all the playlists of this category and their links on Spotify;

- exit - if the user wants to exit the application. 

<img src="https://user-images.githubusercontent.com/81112344/221165604-13e1f93d-5371-4c56-abc9-88fd3e460f53.jpg" width="50%" align="right" />

To navigate through each section's pages, the user can use the following commands:

- next - to go to the next page;

- prev - to go to the previous page;

- exit - to exit the section and get back to the main menu. 

So, I hope you enjoy the app but mostly the grooves...!! 
