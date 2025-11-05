package class2;

interface Operation { //Functional interface - only one method
    float execute(int a, int b);
}

interface MessageProvider {
    String getMessage();
}

//1. traditional/classic
class Addition implements Operation {

    @Override
    public float execute(int a, int b) {
        return a + b;
    }
}

class TraditionalMessageProvider implements MessageProvider{

    @Override
    public String getMessage() {
        return "Traditional Hello :wave";
    }
}

public class Intro {
    public static void main(String[] args) {
        int x = 5, y = 6;
        Operation addition = new Addition();
        System.out.println(addition.execute(x, y));

        //2. anonymous class
        Operation subtraction = new Operation() {
            @Override
            public float execute(int a, int b) {
                return a - b;
            }
        };
        System.out.println(subtraction.execute(x, y));

        //2. lambda expression (only for functional interfaces)
        Operation multiplication = (a, b) -> a * b;
        System.out.println(multiplication.execute(x,y));

        System.out.println("------");

        MessageProvider tmp = new TraditionalMessageProvider();

        //2. anonymous class
        MessageProvider amp = new MessageProvider() {
            @Override
            public String getMessage() {
                return "Anonymous hello - no :wave";
            }
        };

        //3. Lambda expression
        MessageProvider lmp = () -> {

            return "Lambda hello";
        };

        System.out.println(tmp.getMessage());
        System.out.println(amp.getMessage());
        System.out.println(lmp.getMessage());
    }
}
