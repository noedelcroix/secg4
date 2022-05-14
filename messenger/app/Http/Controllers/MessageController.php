<?php

namespace App\Http\Controllers;
use App\Models\Message;
use Illuminate\Http\Request;

class MessageController{


    public static function getMessages($token,$user){
        $response=null;
        try {
            $response=Message::getMessages($token,$user);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json($response);
    }
    public static function postMessage($token, $user, Request $request){
        try {
            $content = $request->post("content");
            Message::postMessage($token,$user,$content);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json(true, 201);

    }

}