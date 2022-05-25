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
    
    public static function addFriend($login, $token){
        
        $response =null;
        try {
            $response=Friends::addFriend($login, $token);     
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
    public static function getPendingInvitations($token){
        $response=null;
        try {
            $response =  Friends::getPendingInvitations($token);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json($response);
    }
}