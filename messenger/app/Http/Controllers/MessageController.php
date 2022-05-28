<?php

namespace App\Http\Controllers;
use App\Models\Message;
use Illuminate\Http\Request;

class MessageController{


    public static function getMessages($user,$token){
        $response=null;
        try {
            $response=Message::getMessages($token,$user);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json($response);
    }
    public static function postMessage($user, $token, Request $request){
        try {
            $content = $request->post("content");
            $replayNumber = $request->post("replayNumber");
            Message::postMessage($token,$user,$content, $replayNumber);
        } catch (Exception $ex) {
            return response()->json(false, 500);
        }
        return response()->json(true, 201);

    }

}