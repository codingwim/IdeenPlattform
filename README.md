# IdeaBase - Ideenplattform

The challenge, called the Ideenplatform, is a platform for ideas, with the help of which ideas
can be submitted, assessed and also used for further implementation. The app enables the user to create ideas, to evaluate or comment ideas of other users
and approve ideas for implementation by specific authorization groups.

## Description

The app is the final challenge of the Mobile App Development - Android course of the Coding School Wörthersee.

## Technologies

* Kotlin
* ViewBinding
* Koin for dependency injection
* Retrofit/OkHttp/Gson for networking
* MVVM as architectural pattern

## Interpretation of the information given

The information given was quite extensive, and the designs were mostly clear. Nevertheless did some questions pop.
See questions below, and our view on it:

* Why do idea managers release the ideas ?
  > To lock an idea so it cant be edited or deleted anymore by the author,after it is already been liked and commented a lot. If not, some ideas might suddenly change in FREE BEER FOR ALL

* Should only released ideas be visible to other users, so they can rate and comment ?

  > all ideas are vidible and can be commented

* Should top ranked show ideas that have no rank or just one rating ?

  > No. Top ranked should only show ideas that have minimum more than 1 rating. We have set a constant for it, default on 3

* Should users see that ideas have been released ? Should idee managers see quickly if an idea has been released ?

  > users no, idea managers yes

* search and filter from one icon click ?

  > yes

## What you might want to know

As the app developed and the minimal viable product was done, we took the liberty to implement some extra features:

* rating icon with fill, shows the average rating the idea has

  * empty hart: not rated yet

  * full hart: average rating above 4.5 / 5

  * partial fill, everything in between


* to inform the Idea managers of the released status we colour the cardview border in yellow if the idea is released

* badges in bottom navigation show if idea status or rating trend has changed since last list display
  * every x seconds (default 15) the idea list gets fetched from the backend (api call)

  * if there are status changes like NEW, UPDATED or RELEASED, a badge for all ideas bottom nav will be show.

    > empty badge (no number) for UPDATED, RELEASED

    > or badge with number of NEW ideas

  * if there are trend changes, a badge for the Top Ranked bottom nav button will be show.

    > empty badge

  * on click auf bottom nav All Ideas:

    > the list gets loaded, sorted by lastupdated, with the STATUS showing in right hand top corner, plus the cardview with dark border

    > the badge gets reset

  * on click auf bottom nav Top Ranked:

    > the list gets loaded, sorted by rating, with POSITION UP or DOWN change showing in right hand top corner

    > the badge gets reset

## Thanks

Too all the motivated teachers / mentors!

## Authors

[Niko Kuschnig](https://gitlab.tailored-apps.com/codingschool_nkuschnig)
[Wim Janssen](https://gitlab.tailored-apps.com/codingschool_wjanssen)