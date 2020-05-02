package com.eatburn.calorieday

class ExampleItem{

    var images: String? = null
    var Meal: String? = null
    var Menu: String? = null
    var Calories: String? = null
    var Date_and_Time: String? = null
    var Latitude: String? = null
    var Longitude: String? = null

    constructor(
        images: String?,
        Meal: String?,
        Menu: String?,
        Calories: String?,
        Date_and_Time: String?,
        Latitude: String?,
        Longitude: String?
    ) {
        this.images = images
        this.Meal = Meal
        this.Menu = Menu
        this.Calories = Calories
        this.Date_and_Time = Date_and_Time
        this.Latitude = Latitude
        this.Longitude = Longitude
    }

    constructor() {}

}

