package com.example.geekmessenger

data class Chatmessage( val id:String,val msg:String,val fromid:String,val toid:String){
    constructor() : this("","","","")
}