# 概要
- MockMVCのサンプルアプリケーション
- MockMVCを用いて簡単な自動テストを行っている
- 中身はタスクのCRUD

# 環境構築

データベースはPostgresqlを用いる。
PostgreSQLに入って、下記のコマンドを実行。

```sql
CREATE DATABASE sample;
ALTER DATABSE sample OWNER TO student;
```
