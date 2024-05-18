# 2nd assignment delivery

## 1. Introduction

This delivery expands the previous one, adding a weblcient that allows the user to interact with all the previous existent features and adding some features with the use of 2 REST APIs.

## 2. Structure

### 2.1. Spring Boot and Controllers

In this delivery a new module was added. This module is composed by a Spring Boot application that serves as a Webclient. This application uses Thymeleaf to render the HTML pages and uses the controllers to handle the requests.
To connect our Spring application to the RMI Gateway we used the RmiClientInterface, that was created in the previous delivery, as a Bean in the Spring application. This Bean is an application scoped resource, meaning that it is created once and shared among all the requests.

This project's Thymeleaf controller has 2 endpoints, one for the landing page and one for the searches page.

On the landing page there is an input field that allows the user to search for a specific keyword or URL. The methods of the Gateway created in the first delivery are used to search words, URLs and index URLs. On the landing page there is also a button that opens a menu where the active barrels and their corresponding average response times and the top 10 seaches are be displayed.

On the search page the features that appear on the landing page are present as well. Besides that, there is a list of the results searched previously, and 2 buttons to travel through the several result pages (divided by 10 items each). All the results have the links that reference that URL in the description.

### 2.2. REST API
In this project requests are sent to 2 different APIs, the Hacker News API and the Link Shortener API. These requests were made using JavaScript.

#### 2.2.1. Hacker News API
The request to the Hacker News API is made when the user searches for a keyword. This API returns the top 500 news and then these results are filtered so that only the ones that contain the keywords are shown. The URLs that have a match are then sent to the gateway to be indexed. For this, a REST endpoint was created in the Webclient that receives a URL and sends it to the Gateway to be indexed.

#### 2.2.2. Link Shortener API
On each each search item there is a button that allows the user to shorten the URL. When the user clicks on it, a request is made to the link shortener API that returns a shortened URL. This shortened URL is then copied to the clipboard.

### 2.3. WebSockets
In this delivery, it was also implemented a Websocket that allows the user to see in real time the administration page. Every time a search is made or a new barrel is created, the administration page is updated simultaneously. This is done using the Bean that implements the ClientInt. When this bean is created, the bean is registered in the Gateway and, when a metric is updated, a RMI callback is used to call a method from the bean that send a message to the Websocket with the metrics. Our JavaScript client listens to the Websocket and updates the page when a message is received providing the user a real time administration page.

## 3. Extras
As an extra the webclient is deployed in the following [url](https://www.wilduit.com/) and can be accessed by anyone.

## 4. Tests

### 4.1. Hacker News API
To test the Hacker News API we searched for specific keyword and by and verified is the indexed urls from hacker news corresponded to that keyword.

### 4.2. Link Shortener API
To test the Link Shortener API, a keyword was searched and was verified if the presented shortened URLs were leading to the correct pages.

### 4.3. WebSockets
To test the Websockets, a new barrel was created and a keyword was searched. Then, it was verified if the administration page was updated in real time. This was tested with multiple computers running at the same time.

### 4.4. Search, indexing and references
To test the search, indexing and references, a URL, whose references to other URLs are known, was indexed. After that, a keyword that was in the indexed URL was searched and it was verified if the references were correct.
To make sure every test case was covered, the following scenarios were also tested: search for already existent URLs and non existent keywords.
