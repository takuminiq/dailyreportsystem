package com.techacademy.constants;

// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // 半角英数字チェックエラー
    HALFSIZE_ERROR,
    // 桁数(8桁~16桁以外)チェックエラー
    RANGECHECK_ERROR,
    // 日付が既に存在するエラー
    DATE_EXISTS_ERROR, // 日付が既に存在するエラー
    //名前の桁数チェックエラー
    NAME_LENGTH_ERROR,
    // タイトルの桁数チェックエラー
    TITLE_LENGTH_ERROR,
    // 内容の桁数チェックエラー
    CONTENT_LENGTH_ERROR,
    // 重複チェックエラー(例外あり)
    DUPLICATE_EXCEPTION_ERROR,
    // 重複チェックエラー(例外なし)
    DUPLICATE_ERROR,
    // ログイン中削除チェックエラー
    LOGINCHECK_ERROR,
    // 日付チェックエラー
    DATECHECK_ERROR,
    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS;

}
