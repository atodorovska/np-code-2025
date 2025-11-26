package class6;

import java.util.*;
import java.util.stream.Stream;

class Attachment {
    String fileName;
    int fileSizeInMb;

    public Attachment(String fileName, int fileSizeInMb) {
        this.fileName = fileName;
        this.fileSizeInMb = fileSizeInMb;
    }
}

class LLMModelDetails {
    String modelName;
    double inputTokenPrice;

    double outputTokenPrice;

    double mbAttachmentPrice;

    public LLMModelDetails(String modelName, double inputTokenPrice, double outputTokenPrice, double mbAttachmentPrice) {
        this.modelName = modelName;
        this.inputTokenPrice = inputTokenPrice;
        this.outputTokenPrice = outputTokenPrice;
        this.mbAttachmentPrice = mbAttachmentPrice;
    }
}


class Interaction {
    String question;
    long timestampQuestion;
    String answer;
    long timestampAnswer;
    List<Attachment> attachments;
    LLMModelDetails modelDetails;

    public Interaction(String question, long timestampQuestion, String answer, long timestampAnswer, List<Attachment> attachments, LLMModelDetails llmModelDetails) {
        this.question = question;
        this.timestampQuestion = timestampQuestion;
        this.answer = answer;
        this.timestampAnswer = timestampAnswer;
        this.attachments = attachments;
        this.modelDetails = llmModelDetails;
    }

    public long processingTime() {
        return timestampAnswer - timestampQuestion;
    }

    public double inputTokens() {
        return Math.ceil(question.length() / 4.0);
    }

    public double outputTokens() {
        return Math.ceil(answer.length() / 4.0);
    }

    public int totalAttachmentsMb() {
        return attachments.stream().mapToInt(i -> i.fileSizeInMb).sum();
    }


    public double price() {
        return inputTokens() * modelDetails.inputTokenPrice + outputTokens() * modelDetails.outputTokenPrice + totalAttachmentsMb() * modelDetails.mbAttachmentPrice;
    }


    @Override
    public String toString() {
        /*
        Q: What is AI?
        Attachments: 2
        A: AI stands for Artificial Intelligence.
        Processing time: 35 Price: 0.05
        * */

        return String.format("Q: %s\nAttachments: %d\nA: %s\nProcessing time: %d Price: %.2f", question, attachments.size(), answer, processingTime(), price());
    }

    public long getTimestampQuestion() {

        return timestampQuestion;
    }

    public long getTimestampAnswer() {
        return timestampAnswer;
    }
}


class Session {
    String sessionId;
    Set<Interaction> interactions;

    public Session(String sessionId) {
        this.sessionId = sessionId;
        interactions = new TreeSet<Interaction>(Comparator.comparing(Interaction::getTimestampQuestion).thenComparing(Interaction::getTimestampAnswer));
    }

    public void addInteraction(Interaction interaction) {
        interactions.add(interaction);
    }

    public void printInteractions() {
        interactions.forEach(System.out::println);
    }

    public double totalInputTokens() {
        return interactions.stream()
                .mapToDouble(Interaction::inputTokens)
                .sum();
    }

    public double totalOutputTokens() {
        return interactions.stream()
                .mapToDouble(Interaction::outputTokens)
                .sum();
    }

    public double totalPrice() {
        return interactions.stream()
                .mapToDouble(Interaction::price)
                .sum();
    }

    public long totalProcessingTime() {
        return interactions.stream()
                .mapToLong(Interaction::processingTime)
                .sum();
    }

    public int totalAttachmentsCount() {
        return interactions.stream()
                .mapToInt(i -> i.attachments.size())
                .sum();
    }

    public int totalAttachmentsMb() {
        return interactions.stream()
                .mapToInt(Interaction::totalAttachmentsMb)
                .sum();
    }

    public void printDetails() {
        /*
        Session ID: session1
        Interactions: 3
        Input tokens: 15
        Output tokens: 45
        Price: 0.22
        Processing time: 175
        Number of attachments: 3
        Total attachment size: 9
        * */
        System.out.println("Session ID: " + sessionId);
        System.out.println("Interactions: " + interactions.size());
        System.out.println(String.format("Input tokens: %.0f", totalInputTokens()));
        System.out.println(String.format("Output tokens: %.0f", totalOutputTokens()));
        System.out.println(String.format("Price: %.2f", totalPrice()));
        System.out.println(String.format("Processing time: %d", totalProcessingTime()));
        System.out.println(String.format("Number of attachments: %d", totalAttachmentsCount()));
        System.out.println(String.format("Total attachment size: %d", totalAttachmentsMb()));
    }
}

class User {
    String userId;
    Map<String, Session> sessions;

    public User(String userId) {
        this.userId = userId;
        sessions = new HashMap<>();
    }

    public void addInteraction(String sessionId, String question, long timestampQuestion, String answer, long timestampAnswer, List<Attachment> attachments, LLMModelDetails llmModelDetails) {
        sessions.putIfAbsent(sessionId, new Session(sessionId));

        Session s = sessions.get(sessionId);

        s.addInteraction(new Interaction(question, timestampQuestion, answer, timestampAnswer, attachments, llmModelDetails));
    }

    public void printDetails() {
        sessions.values()
                .stream()
                .sorted(Comparator.comparing(Session::totalAttachmentsCount).thenComparing(Session::totalPrice).reversed())
                .forEach(s -> s.printDetails());
    }

    public Interaction longestProcessingTimeInteractions() {
//        List<Interaction> interactions = new ArrayList<>();
//        for (Session s : sessions.values()) {
//            for (Interaction interaction : s.interactions) {
//                interactions.add(interaction);
//            }
//        }
//
//        Interaction longest = interactions.get(0);
//
//        for (Interaction interaction : interactions) {
//            if (interaction.processingTime() > longest.processingTime()) {
//                longest = interaction;
//            }
//        }
//
//        return longest;


        return sessions.values().stream()
                .flatMap(session -> session.interactions.stream())
                .max(Comparator.comparing(Interaction::processingTime))
                .get();


    }

    public Interaction mostExpensiveInteractions() {
        return sessions.values().stream()
                .flatMap(session -> session.interactions.stream())
                .max(Comparator.comparing(Interaction::price))
                .get();
    }
}

class FileNotSupportedException extends Exception {
    public FileNotSupportedException(String message) {
        super(message);
    }
}

class AttachmentsSizeExceededException extends Exception {
    public AttachmentsSizeExceededException(String message) {
        super(message);
    }
}

class Chatbot {

    LLMModelDetails llmModelDetails;
    List<String> notSupportedFiles;
    int allowedAttachmentsSize;
    Map<String, User> users;

    Chatbot(LLMModelDetails llmModelDetails, List<String> notSupportedFiles, int allowedAttachmentsSize) {
        this.llmModelDetails = llmModelDetails;
        this.notSupportedFiles = notSupportedFiles;
        this.allowedAttachmentsSize = allowedAttachmentsSize;
        users = new TreeMap<>();

    }

    public void addInteraction(String userId, String sessionId, String question, long timestampQuestion, String answer, long timestampAnswer, List<Attachment> attachments) throws AttachmentsSizeExceededException, FileNotSupportedException {

//        int sum = 0;
//        for (Attachment attachment : attachments) {
//            sum += attachment.fileSizeInMb;
//        }

        int sum = attachments.stream()
                .mapToInt(a -> a.fileSizeInMb)
                .sum();

        if (sum > allowedAttachmentsSize) {
            throw new AttachmentsSizeExceededException("exceed");
        }

        for (String extension : notSupportedFiles) {
            for (Attachment attachment : attachments) {
                if (attachment.fileName.endsWith(extension)) {
                    throw new FileNotSupportedException(extension);
                }
            }
        }


        users.putIfAbsent(userId, new User(userId));

        User user = users.get(userId);

        user.addInteraction(sessionId, question, timestampQuestion, answer, timestampAnswer, attachments, llmModelDetails);

    }

    public void printConversation(String userId, String sessionId) {
        users.get(userId).sessions.get(sessionId).printInteractions();
    }

    public void printSessionDetails(String userId, String sessionId) {


        users.get(userId).sessions.get(sessionId).printDetails();
    }

    public void printUserDetails(String userId) {
        System.out.println("User: " + userId);
        System.out.println("Sessions:");
        users.get(userId).printDetails();
    }

    public void longestProcessingTimeInteractions() {
        users.values().forEach(u -> {
            System.out.println("User: " + u.userId);
            System.out.println("Longest processing time interaction:");
            System.out.println(u.longestProcessingTimeInteractions());
        });
    }

    public void mostExpensiveInteractions() {
        users.values().forEach(u -> {
            System.out.println("User: " + u.userId);
            System.out.println("Most expensive interaction:");
            System.out.println(u.mostExpensiveInteractions());
        });
    }
}


public class ChatbotTest2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read LLMModelDetails properties
        String modelName = scanner.next();
        double inputTokenPrice = scanner.nextDouble();
        double outputTokenPrice = scanner.nextDouble();
        double mbAttachmentPrice = scanner.nextDouble();

        LLMModelDetails llmModelDetails = new LLMModelDetails(modelName, inputTokenPrice, outputTokenPrice, mbAttachmentPrice);

        // Read list of notSupportedFiles
        scanner.nextLine(); // Consume newline
        List<String> notSupportedFiles = Arrays.asList(scanner.nextLine().split(";"));

        // Read allowedAttachmentsSize
        int allowedAttachmentsSize = scanner.nextInt();
        scanner.nextLine();

        Chatbot chatbot = new Chatbot(llmModelDetails, notSupportedFiles, allowedAttachmentsSize);

        while (scanner.hasNext()) {
            String[] parts = scanner.nextLine().split(";");
            String command = parts[0];

            switch (command) {
                case "addInteraction": {
                    try {
                        String userId = parts[1];
                        String sessionId = parts[2];
                        String question = parts[3];
                        long timestampQuestion = Long.parseLong(parts[4]);
                        String answer = parts[5];
                        long timestampAnswer = Long.parseLong(parts[6]);
                        int attachmentCount = Integer.parseInt(parts[7]);
                        List<Attachment> attachments = new ArrayList<>();

                        for (int i = 0; i < attachmentCount; i++) {
                            String fileName = parts[8 + i * 2];
                            int fileSize = Integer.parseInt(parts[9 + i * 2]);
                            attachments.add(new Attachment(fileName, fileSize));
                        }

                        chatbot.addInteraction(userId, sessionId, question, timestampQuestion, answer, timestampAnswer, attachments);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "printConversation": {
                    String userId = parts[1];
                    String sessionId = parts[2];
                    chatbot.printConversation(userId, sessionId);
                    break;
                }
                case "printSessionDetails": {
                    String userId = parts[1];
                    String sessionId = parts[2];
                    chatbot.printSessionDetails(userId, sessionId);
                    break;
                }
                case "printUserDetails": {
                    String userId = parts[1];
                    chatbot.printUserDetails(userId);
                    break;
                }
                case "longestProcessingTimeInteractions": {
                    chatbot.longestProcessingTimeInteractions();
                    break;
                }
                case "mostExpensiveInteractions": {
                    chatbot.mostExpensiveInteractions();
                    break;
                }
                case "exit": {
                    return;
                }
            }
        }

        scanner.close();
    }
}

