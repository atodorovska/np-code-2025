To organize the electoral process for parliamentary elections, it is necessary to implement an application for entering results from polling stations that will have functionality to calculate the number of seats won by electoral units. The elections are organized within several electoral units (for example, 6 in North Macedonia). Each electoral unit has polling stations where citizens vote. After the elections are completed, each electoral unit is allocated the same number of mandates (20 seats from each electoral unit in North Macedonia).

For this purpose, the following classes need to be implemented:

---

### 1. `VotesController`
A class that will be used by members of polling stations to enter votes by party from their polling stations. For the class, the following methods should be defined:

- `VotesController(List<String> parties, Map<String, Integer> unitPerPoll)`  
  Constructor with two arguments: a list of political parties registered to participate in the elections and a map where the key is the polling station ID, and the value is the number of the electoral unit to which the corresponding polling station belongs.

- `void addElectionUnit(ElectionUnit electionUnit)`  
  Method for adding an electoral unit to the controller.

- `void addVotes(String pollId, String party, int votes)`  
  Method for adding the number of votes `votes` for the political party `party` from the polling station `pollId`. The method should throw an exception of type `InvalidVotesException` if the party is not registered to participate in the elections.

---

### 2. Class `ElectionUnit`
Class for representing an electoral unit. For the class, the following should be implemented:

- `ElectionUnit(int unit, Map<String, Integer> votersByPoll)`  
  Constructor with two arguments: the serial number of the electoral unit and a map where the key is the polling station ID, and the value is the number of registered voters in that polling station.

- `void addVotes(String pollId, String party, int votes)`  
  Method for adding the number of votes `votes` for the political party `party` from the polling station `pollId`.

- `void subscribe(Subscriber subscriber)`  
  Method for subscribing an observer to the election results from this electoral unit.

- `void unsubscribe(Subscriber subscriber)`  
  Method for unsubscribing an observer `subscriber`.

---

### 3. Interface `Subscriber`
With one method:

- `void updateVotes(int unit, String pollId, String party, int votes, int totalVotersPerPoll, int totalVotersPerUnit)`  
  Method for updating votes in the observer with information about the electoral unit number, polling station ID, political party, votes for the political party, total number of voters in the polling station, and total number of voters in the electoral unit.

---

### 4. Class `VotersTurnoutApp`
Represents one type of observer of electoral units that calculates voter turnout statistics per electoral unit. The class should implement the method:

- `printStatistics()`  
  Prints turnout statistics in the following format:  
  `Unit: Casted: Voters: Turnout:`  
  The exact format is: `%10d %7d %7d %7.2f`.

---

### 5. Class `SeatsApp`
Represents another type of observer of electoral units that calculates the number of parliamentary seats won by parties per electoral unit, as well as the total number of seats in the electoral units it observes. The class should implement the method:

- `void printStatistics()`  
  Prints statistics for won parliamentary seats in the following format:  
  `Party Votes %Votes Seats %Seats`  
  where `Party` is the name of the party, `Votes` represents the number of votes at the national level (from all electoral units), and `Seats` represents the number of parliamentary mandates won at the national level. Political parties should be sorted by the number of votes won at the national level in descending order.  
  The exact printing format is: `%10s %5d %7.2f%% %5d %7.2f%%`.

- The calculation of mandates within one electoral unit is done using the method shown in the image below. First, the number of votes required for one parliamentary mandate is calculated (if the number of mandates in the electoral unit is 20):  
  `1800 votes / 20 mandates = 90 votes/mandate`.  
  Then, the votes for the parties are divided (integer division) by the required votes per mandate (90), and the quotient represents the number of mandates won by that party. The undistributed mandates (in the example, 1 mandate, since 19 were distributed in total) are assigned to the party with the highest number of votes (party A receives a total of 12 mandates).
