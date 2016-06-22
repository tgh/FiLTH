package com.filth.test.util;

import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * This class can be used to generate random movie titles. It uses it's own
 * dictionary of words so that the titles aren't <i>completely</i> unrealistic
 * nonsense.
 */
public class RandomMovieTitleGenerator {
    
    private static final int PRESENT_VERBS_INDEX = 0;
    private static final int PAST_VERBS_INDEX = 1;
    private static final int CONSONANT_NOUNS_INDEX = 0;
    private static final int VOWEL_NOUNS_INDEX = 1;
    private static final int CONSONANT_ADJECTIVES_INDEX = 0;
    private static final int VOWEL_ADJECTIVES_INDEX = 1;
    
    
    /* Words may be repeated to increase probability of being selected */
    
    private static final String[] FIRST_WORD = { "The", "The", "The", "The", "The", "A", "A",
        "An", "An", "", "", "", "", "My", "Our", "Their", "His", "Her", "The", "The"
    };
    private static final String[] PREPOSITIONS = { "in", "at", "on", "Over", "After", "to",
        "of", "Under", "a", "an", "From", "Through", "Before"
    };
    private static final String[] CONJUNCTIONS = { "for", "and", "and", "and", "but", "or" };
    private static final String[][] NOUNS = {
        //starts with consonant
        { "Man", "Woman", "Kid", "Dog", "War", "House", "Family", "Race", "Person",
          "Water", "Fire", "Music", "Baker", "Cook", "Food", "Doctor", "Lawyer", "Number",
          "Singer", "Programmer", "Criminal", "Crime", "Cop", "Car", "Truck", "Boat",
          "River", "Ocean", "Country", "Smile", "Face", "Horse", "Penguin", "Pig", "Citizen",
          "Missiles", "Death", "Life", "Boss", "He", "She", "You", "They", "Michael",
          "Paul", "Peter", "Ruth", "John", "John", "Jane", "Revolution", "Magic", "Cloud",
          "Mother", "Father", "Desk", "Balloon", "Machine", "Politics", "Education",
          "Robot", "Zombie", "Monster", "Future", "Past", "Day", "Night", "School",
          "Dawn", "Stair", "Ladder", "Circus", "Clown", "Street", "Road", "City", "Sound",
          "Town", "Film", "Countryside", "Hotel", "Motorcycle", "Company", "Corporation",
          "Water", "Money", "Baseball", "Football", "Hockey", "Rope", "Tree", "Wind",
          "Insect", "Raven", "Bird", "Camel", "Smoke", "Spider", "Snake", "We", "Radio",
          "Shark", "Sea", "California", "Brooklyn", "Lake", "Sun", "Child", "Blood", "Fish",
          "Mountain", "Hill", "Mission", "Dad", "Mom", "Brother", "Sister", "Husband", "Wife"
        },
        //starts with vowel
        { "Earth", "Idea", "It", "I", "Everything", "Eve", "Esther", "Alien", "Index",
          "Animal", "Elephant", "Albatross", "Ice", "Eagle", "Onyx", "Orange", "Apple",
          "Angel", "Angle", "Upstairs", "Understanding", "Overture", "Opening", "Ox",
          "Appointment", "Attitude", "Employee", "Eye", "Item", "Oregon", "Air", "Airplane",
          "Operation", "Office", "Uncle", "Aunt", "Ant", "Ape", "Ending", "End", "Exit",
          "Everyone", "Ether", "Airport", "Igloo", "Eskimo", "Iceland", "Alaska", "Italy",
          "Escape", ""
        }
    };
    private static final String[][] VERBS = {
        //present
        { "Is", "Runs", "Goes", "Walks", "Says", "Touches",
          "Hears", "Sees", "Believes", "Loves", "Stays", "Leaves", "Pays", "Leads", "Laughs", "Smiles",
          "Speaks", "Hurts", "Kills", "Saves", "Grows", "Drives", "Flies", "Swims", "Takes", "Loves",
          "is", "Writes", "Makes", "Cooks", "Sells", "Steals", "Falls", "Calls", "Travels", "is",
          "Loves", "is", "Sings", "Put", "Wonders", "Thinks", "Escapes", "Likes", "Likes"
        },
        //past
        { "Was", "Ran", "Went", "Walked", "Said", "Touched", "Heard", "Saw", "Believed", "Loved",
          "Stayed", "Left", "Paid", "Led", "Laughed", "Smiled",
          "Spoke", "Hurt", "Killed", "Saved", "Grew", "Drove", "Flew", "Swam", "Took", "Wrote", "Made",
          "Cooked", "Sold", "Sang", "Stole", "Fell", "Called", "Traveled", "Put", "Thought", "Escaped",
          "Liked", "Quit", "Died"
        }
    };
    private static final String[][] ADJECTIVES = {
        //starts with consonant
        { "Beautiful", "Weird", "Heavy", "Loud", "Light", "Crazy", "Mean", "Wonderful",
          "Magical", "Scary", "Horrible", "Stellar", "Broken", "Funny", "Narrow", "Dry",
          "Big", "Small", "Wide", "Black", "White", "Red", "Blue", "Green", "Yellow",
          "Gray", "Brown", "Purple", "Dark", "Sharp", "Bloody", "High", "Low", "Terrible",
          "Lovely", "Nice", "Wild", "Hard", "Long", "Short"
        },
        //starts with vowel
        { "Incredile", "Amazing", "Easy", "Ugly", "Orange", "Inner", "Inside", "Impossible",
          "Indescribable", "Undisputed", "Inconvenient", "Unbroken", "Undefeated", "Adventurous",
          "Awesome", "Azure", "Ultimate", "Ultraviolet", "Unbearable", "Unbreakable", "Unchanged",
          "Uncomfortable", "Obtrusive", "Obtuse", "Obese", "Offensive", "Opaque", "Obnoxious",
          "Outstanding", "Open", "Accidental", "Official", "Omniscient"
        }
    };
    
    public static String generateTitle() {
        Random random = new Random();
        //get random number for number of words (restricting from 1 to 6).
        //random can return 0, hence adding 1.
        int numWords = random.nextInt(3) + 1;
        switch (numWords) {
            case 1:
                return generate1WordTitle();
            case 2:
                return generate2WordTitle();
            case 3:
                return generate3WordTitle();
            case 4:
                return generate4WordTitle();
            case 5:
                return generate5WordTitle();
            case 6:
                return generate6WordTitle();
            default:
                //will never get here--this is just here to satisfy the compiler
                return "The Movie";
        }
    }
    
    public static String generate1WordTitle() {
        return getRandomNoun();
    }
    
    public static String generate2WordTitle() {
        String firstWord = getRandomFirstWord();
        String secondWord;
        
        switch (firstWord) {
            case "":
                firstWord = getRandomNoun();
                secondWord = getRandomVerb();
                break;
            case "An":
                secondWord = getRandomVowelNoun();
                break;
            case "A":
                secondWord = getRandomConsonantNoun();
                break;
            default:
                secondWord = getRandomNoun();
                break;
        }
        
        return constructTitle(firstWord, secondWord);
    }
    
    public static String generate3WordTitle() {
        String firstWord = getRandomFirstWord();
        String secondWord;
        String thirdWord;
        
        switch (firstWord) {
            case "":
                //<noun> <verb> <noun>
                firstWord = getRandomNoun();
                secondWord = getRandomVerb();
                thirdWord = getRandomNoun();
                break;
            case "An":
                //An <vowel adjective> <noun>
                if (decideTrue()) {
                    secondWord = getRandomVowelAdjective();
                    thirdWord = getRandomNoun();
                }
                //An <vowel noun> <verb>
                else {
                    secondWord = getRandomVowelNoun();
                    thirdWord = getRandomVerb();
                }
                break;
            case "A":
                //A <consonant adjective> <noun>
                if (decideTrue()) {
                    secondWord = getRandomConsonantAdjective();
                    thirdWord = getRandomNoun();
                } 
                //A <consonant noun> <verb>
                else {
                    secondWord = getRandomVowelNoun();
                    thirdWord = getRandomVerb();
                }
                break;
            default:
                //<first word> <adjective> <noun>
                if (decideTrue()) {
                    secondWord = getRandomAdjective();
                    thirdWord = getRandomNoun();
                }
                //<first word> <noun> <verb>
                else {
                    secondWord = getRandomNoun();
                    thirdWord = getRandomVerb();
                }
                break;
        }
        
        return constructTitle(firstWord, secondWord, thirdWord);
    }
    
    public static String generate4WordTitle() {
        return null;
    }
    
    public static String generate5WordTitle() {
        return null;
    }
    
    public static String generate6WordTitle() {
        return null;
    }
    
    private static String getRandomFirstWord() {
        Random random = new Random();
        int idx = random.nextInt(FIRST_WORD.length);
        return FIRST_WORD[idx];
    }
    
    private static String getRandomNoun() {
        Random random = new Random();
        int nounsIdx = random.nextInt(2);
        if (nounsIdx == CONSONANT_NOUNS_INDEX) {
            return getRandomConsonantNoun();
        }
        return getRandomVowelNoun();
    }
    
    private static String getRandomConsonantNoun() {
        Random random = new Random();
        int idx = random.nextInt(NOUNS[CONSONANT_NOUNS_INDEX].length);
        return NOUNS[CONSONANT_NOUNS_INDEX][idx];
    }
    
    private static String getRandomVowelNoun() {
        Random random = new Random();
        int idx = random.nextInt(NOUNS[VOWEL_NOUNS_INDEX].length);
        return NOUNS[VOWEL_NOUNS_INDEX][idx];
    }
    
    private static String getRandomVerb() {
        Random random = new Random();
        int verbsIdx = random.nextInt(2);
        if (verbsIdx == PRESENT_VERBS_INDEX) {
            return getRandomPresentVerb();
        }
        return getRandomPastVerb();
    }
    
    private static String getRandomPresentVerb() {
        Random random = new Random();
        int idx = random.nextInt(VERBS[PRESENT_VERBS_INDEX].length);
        return VERBS[PRESENT_VERBS_INDEX][idx];
    }
    
    private static String getRandomPastVerb() {
        Random random = new Random();
        int idx = random.nextInt(VERBS[PAST_VERBS_INDEX].length);
        return VERBS[PAST_VERBS_INDEX][idx];
    }
    
    private static String getRandomAdjective() {
        Random random = new Random();
        int nounsIdx = random.nextInt(2);
        if (nounsIdx == CONSONANT_ADJECTIVES_INDEX) {
            return getRandomConsonantAdjective();
        }
        return getRandomVowelAdjective();
    }
    
    private static String getRandomConsonantAdjective() {
        Random random = new Random();
        int idx = random.nextInt(ADJECTIVES[CONSONANT_ADJECTIVES_INDEX].length);
        return ADJECTIVES[CONSONANT_ADJECTIVES_INDEX][idx];
    }
    
    private static String getRandomVowelAdjective() {
        Random random = new Random();
        int idx = random.nextInt(ADJECTIVES[VOWEL_ADJECTIVES_INDEX].length);
        return ADJECTIVES[VOWEL_ADJECTIVES_INDEX][idx];
    }
    
    private static boolean decideTrue() {
        Random random = new Random();
        return random.nextBoolean();
    }
    
    private static String constructTitle(String ... words) {
        return StringUtils.join(words, " ");
    }

}
