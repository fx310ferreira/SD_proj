# 2nd assignment delivery

## 1. Introduction

This delivery expands the previous one, adding a weblcient that allows the user to interact with all the preious existent features and adding some features with the use of 2 REST APIs.

## 2. Structure

### 2.1. Spring Boot and Controllers

In this delivery a new module was added this module is composed by a Spring Boot application that serves as a webclient. This application uses thymeleaf to render the html pages and uses the controllers to handle the requests.
To connect our Spring application to the RMI gateway we used the RmiClientInterface, that was created in the previous delivery, as a Bean in the Spring application. This Bean is an application scoped resource, meaning that it is created once and shared among all the requests.

In our thymeleaf controller we have 2 endpoints, one for the landing page and one for the search page.

On the landing page we have an input field that allows the user to search for a specific keyword or url, we use the methods of the gateway created in the first delivery to search words, urls and index urls. Also on the landing page is present a button that allows the user to open a menu where the user can see the top 10 seaches, the active barrels and the average response time per barrel.

On the search page we have the same features as the landing page but we also have a list of the results searched previously, and 2 buttons to travel through the several result pages that are divided by 10 results each. On each URL box we can se the links that reference that url.

### 2.2. REST API
In our project we make our api requests to 2 different APIs, the Hacker News API and the link shortener API, both of this requests are made using javascript.

#### 2.2.1. Hacker News API
The request to the Hacker News API is made when the user searches for a keyword, the API returns the top 500 news and then we filter the results be ones that contain the keyword. The urls that match the keyword are then sent to the gateway to be indexed. to do this we created a Rest endpoint in the gateway that receives a URL and sends it to the gateway to be indexed.

#### 2.2.2. Link Shortener API
On each result of the search we present a button that allows the user to shorten the URL, this button makes a request to the link shortener API that returns a shortened URL. This shortened URL is then copied to the clipboard.

### 2.3. WebSockets
In this delivery we also implemented a websocket that allows the user to see in real time the administration page. Every time a search is made or a new barrel is created the administration page is updated in real time. This is done using the Bean. The Bean implements the ClientInt, when the bean is created we register the bean in the gateway, and when any metric is updated RMI callback is used to call a method from the bean that send a message to the websocket with the metrics. Our javascript client listens to the websocket and updates the page when a message is received. Giving us the real time administration page.

## 3. Extras
As and extra the webclient is deployed in the following [url](https://wilduit.com/) and can be accessed by anyone. 

## 4. Tests

### 4.1. Hacker News API
To test the Hacker News API we searched for specific keyword and by and verified is the indexed urls from hacker news corresponded to that keyword.

### 4.2. Link Shortener API
To test the Link Shortener API we searched for a keyword and then shortened the URL, we then verified if the shortened URL was leading to the correct page.

### 4.3. WebSockets
To test the websockets we created a new barrel and searched for a keyword, we then verified if the administration page was updated in real time. This was tested between multiple computers. Using multiple clients at the time.

### 4.4. Search, indexing and references
To test the search, indexing and references we indexed a url that we know has references to other urls, we then searched for a keyword that was present in the indexed url and verified if the references were correct.
We also tested to search for already existent urls and non existent keywords to make sure every case was covered.