package class9;

import java.util.ArrayList;
import java.util.List;

interface NewsSubscriber {
    void update(String article);
}

class User implements NewsSubscriber {

    String username;
    List<String> receivedNews;

    public User(String username) {
        this.username = username;
        receivedNews = new ArrayList<>();
    }


    @Override
    public void update(String article) {
        receivedNews.add(article);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", receivedNews=" + receivedNews +
                '}';
    }
}

class NewsOutlet {
    List<String> articles;
    List<NewsSubscriber> newsSubscribers;

    public NewsOutlet() {
        articles = new ArrayList<>();
        newsSubscribers = new ArrayList<>();
    }

    public void subscribe(NewsSubscriber newsSubscriber) {
        newsSubscribers.add(newsSubscriber);
    }

    public void unsubscribe(NewsSubscriber newsSubscriber) {
        newsSubscribers.remove(newsSubscriber);
    }

    public void notifySubscribers(String article) {
        for (NewsSubscriber newsSubscriber : newsSubscribers) {
            newsSubscriber.update(article);
        }
    }

    public void addArticle(String article) {
        articles.add(article);
        notifySubscribers(article);
    }

    @Override
    public String toString() {
        return "NewsOutlet{" +
                "articles=" + articles +
                '}';
    }
}

public class ObserverTest {
    public static void main(String[] args) {
        NewsSubscriber s1 = new User("stefan");
        NewsSubscriber s2 = new User("ana");

        NewsOutlet outlet = new NewsOutlet();
        outlet.subscribe(s1);
        outlet.subscribe(s2);

        for (int i=0;i<100;i++){
            String article = "article" + i;
            outlet.addArticle(article);

            if (i==33){
                outlet.unsubscribe(s1);
            }
            if (i==82){
                outlet.subscribe(s1);
            }
        }

        System.out.println(outlet);


        System.out.println(s1);

        System.out.println(s2);
    }
}
