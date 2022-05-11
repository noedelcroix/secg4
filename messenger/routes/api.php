<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});
Route::post('/auth',[ConnectionController::class,'getChannels']);

Route::get('/channels/{chatRoomId}/messages',[ChatController::class,'getMessages']);

Route::post('/channels/{chatRoomId}/messages',[ChatController::class,'postMessage']);


Route::get('/createaccount',[RegisterController::class,'getAccounts']);

Route::post('/createaccount',[RegisterController::class,'createAccount']);