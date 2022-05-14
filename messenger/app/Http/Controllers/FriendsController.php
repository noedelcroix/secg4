<?php

namespace App\Http\Controllers;
use App\Models\Friends;
use Illuminate\Http\Request;

class FriendsController{

    public static function getFriends($token){
        $response=null;
        try {
            $response = Friends::getFriends($token);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json($response);
    }
    
    public static function getOnlineFriends($token){
        $response=null;
        try {
            $response =  Friends::getOnlineFriends($token);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json($response);
    }
    
    public static function addFriend($token,$login){
        $response =null;
        try {
            $response=Friends::addFriend($token,$login);     
           } catch (Exception $ex) {
            return response()->json(false, 500);
        }        
        return response()->json($response);
    }

    public static function delFriend($token,$login){
        try {
            Friends::delFriend($token,$login); 
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }     
        return response()->json(false, 201);   
        
        
    }

}