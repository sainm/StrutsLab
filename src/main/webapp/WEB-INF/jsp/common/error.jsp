<%@ page contentType="text/html; charset=UTF-8" isErrorPage="true" %>
<%@ page import="com.strutslab.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>エラー</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
<div style="width:500px;margin:100px auto;border:2px solid #c33;padding:24px;">
<h2 style="color:#c33;">エラーが発生しました</h2>
<p>申し訳ありませんが、システムエラーが発生しました。</p>
<p><a href="<%=request.getContextPath()%>/login.do">ログイン画面に戻る</a></p>
</div>
</body>
</html>
