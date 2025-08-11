package lib.model;

/*

{
    "userId": 1,
    "id": 1,
    "title": "delectus aut autem",
    "completed": false
  }
*/
public class Todo {
    public  int id;
    public  int userId;
    public  String title;
    public  boolean completed;

    @Override
    public String toString() {
        return "Todo{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed  +
                '}';
    }
}
