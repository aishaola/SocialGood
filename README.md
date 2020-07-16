# SocialGood

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An social media app that gives people a space to promote their social justice interests, feature link displays and what the links are for and highlights their issue of choice. People can follow/share pages of issues they care about. Fundraising can be done through app

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social Network
- **Mobile:** Mobile is useful for an instant resource/information sharing tool. Camera can be used to add media to posts made. 
- **Story:** Creates a space where people can be vocal about the issues they care about. Allows people to flexibly share various forms of media together in a way they feel most fitting. In a country where people of feeling the need to be more political, an app that highlights activism could be helpful.
- **Market:** Any one who is interested in activism or wants to learn more about what they can do to help other people. Organizations can also use the app to promote themselves and bring more awareness for the issues they seek to solve. 
- **Habit:** People might check in daily on the categories they follow. Activists may make a habit out of updating their resources. The social aspect to the app is also habit inducing, people may log in to check what they're friends are sharing. 
- **Scope:** Location services can be used so users can tune in to issues that are solely community wide and/or look for nearby protests.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create accounts/profiles and customize it to their liking
   * User can add profile picture
   * Can add affiliation between profile and certain social justice categories and be display on profile page
   * User can add bio
   * User can delete posts from profile
   * User stays logged in once app terminates
* Users can create posts
   * Users can post images or links, and add a caption to the post
   * Users can add categories to associate post with
   * Posts show username, categories associated, timestamp, caption, and pictures/clickable links
   * Clicking on posts will launch A PostDetail Activity.
* Users can view posts on their homepage feed that they've subscribed to
   * Users can subscribe to categories of interest
   * Users can follow other accounts
   * Posts from categories subscibed and followed will show up on feed
   * Pinch to scale images
   * Links launch the url in a browser
* Users can 'reshare' posts onto their own profile
* Profiles followed will show up on their feed
* Google Maps SDK allows for users to add location to their profile, allowong people who are close who care about local issues to organize
* Algorithm: Search for posts based on categories/interests 
* App uses an animation (e.g. fade in/out, e.g. animating a view growing and shrinking)
* App incorporates an external library to add visual polish

**Optional Nice-to-have Stories**

* Users can make mixed media posts
* Users can have pinned content
* Users can save posts to refer back to later
* New users are taken to a page that prompts them to follow accounts listed under categorires they are interested in 
* Users can fundraise through the app
* User bio can be built on to include more types of information
* Fundraising type of post can be created
* Google maps API can be used to find issues local to user's area 
* Google maps API can be used to create protest map/create events  

### 2. Screen Archetypes

* Introduction, Sign in or log-in screen
* Sign in activity
   * Sign-in functionality
* Login activity
   * Login functionalities
* Homepage
   * most recent posts that people you follow have posted
   * If username is pressed user can see poster's account
   * Links are clickable and lanches the url
* Profile Fragment
   * Edit profile pic
   * Edit profile categories of interest
   * See posts they've created
   * Display how many other users 'tune in' to each account
* Detail View for each post
   * Allows user to reshare post
* Create post Fragment
   * Attach topic post falls under
   * post an image with camera
   * post an image from gallery picker
   * post and link and link desciption
* Search fragment
   * Algorithm: Search for posts based on categories/interests 
   * Algorithm: Search for profiles based on category/interest
   

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* [fill out your first tab]
* [fill out your second tab]
* [fill out your third tab]

**Flow Navigation** (Screen to Screen)

* [list first screen here]
   * [list screen navigation here]
   * ...
* [list second screen here]
   * [list screen  here]
   * ...navigation

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://github.com/aishaola/SocialGood/blob/master/IMG_3455.jpeg" width=600>
<img src="https://github.com/aishaola/SocialGood/blob/master/IMG_3456.jpeg" width=600>
<img src="https://github.com/aishaola/SocialGood/blob/master/IMG_3457.jpeg" width=600>
<img src="https://github.com/aishaola/SocialGood/blob/master/IMG_3458.jpeg" width=600>
<img src="https://github.com/aishaola/SocialGood/blob/master/IMG_3459.jpeg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
* Posts class
* Users class
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
