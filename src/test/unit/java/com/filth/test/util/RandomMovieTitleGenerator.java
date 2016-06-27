package com.filth.test.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * This class can be used to generate random movie titles. It uses it's own
 * dictionary of words so that the titles aren't <i>completely</i> unrealistic
 * nonsense.
 */
public class RandomMovieTitleGenerator {
    
    private static final Determiner DETERMINER = Determiner.getInstance();
    private static final Preposition PREPOSITION = Preposition.getInstance();
    private static final Conjunction CONJUNCTION = Conjunction.getInstance();
    private static final Noun NOUN = Noun.getInstance();
    private static final Verb VERB = Verb.getInstance();
    private static final Adjective ADJECTIVE = Adjective.getInstance();
    
    private static final Word[][] TITLE_PATTERNS = {
        //1-word
        { NOUN },
        { ADJECTIVE },
        //2-word
        { DETERMINER, NOUN },
        { NOUN, VERB },
        { ADJECTIVE, NOUN },
        { PREPOSITION, NOUN },
        //3-word
        { DETERMINER, NOUN, VERB },
        { NOUN, CONJUNCTION, NOUN },
        { DETERMINER, ADJECTIVE, NOUN },
        { NOUN, PREPOSITION, NOUN },
        //4-word
        { DETERMINER, NOUN, CONJUNCTION, NOUN },
        { DETERMINER, ADJECTIVE, NOUN, VERB },
        { NOUN, CONJUNCTION, DETERMINER, NOUN },
        { NOUN, PREPOSITION, DETERMINER, NOUN },
        { NOUN, VERB, PREPOSITION, NOUN },
        //5-word
        { NOUN, CONJUNCTION, DETERMINER, ADJECTIVE, NOUN },
        { DETERMINER, ADJECTIVE, NOUN, CONJUNCTION, NOUN },
        { ADJECTIVE, NOUN, CONJUNCTION, ADJECTIVE, NOUN },
        { NOUN, VERB, PREPOSITION, DETERMINER, NOUN },
        { DETERMINER, NOUN, VERB, PREPOSITION, NOUN }
    };
    
    public static String generateTitle() {
        //get random title pattern
        Random random = new Random();
        int idx = random.nextInt(TITLE_PATTERNS.length);
        Word[] pattern = TITLE_PATTERNS[idx];
        boolean nextWordVowel = false;
        boolean nextWordConsonant = false;
        String randomWord;
        
        //construct title with random words in the pattern
        List<String> titleWords = new ArrayList<>();
        for (Word word : pattern) {
            if (nextWordVowel && word instanceof Noun) {
                randomWord = ((Noun) word).getRandomVowelNoun();
                nextWordVowel = false;
            } else if (nextWordConsonant && word instanceof Noun) {
                randomWord = ((Noun) word).getRandomConsonantNoun();
                nextWordConsonant = false;
            } else if (nextWordVowel && word instanceof Adjective) {
                randomWord = ((Adjective) word).getRandomVowelAdjective();
                nextWordVowel = false;
            } else if (nextWordConsonant && word instanceof Adjective) {
                randomWord = ((Adjective) word).getRandomConsonantAdjective();
                nextWordConsonant = false;
            } else {
                randomWord = word.getRandomWord();
                
                //flag next word to start with consonant if this is "A"
                if (randomWord.equals("A")) {
                    nextWordConsonant = true;
                }
                //flag next word to start with vowel if this is "An"
                else if (randomWord.equals("An")) {
                    nextWordVowel = true;
                }
            }
            
            titleWords.add(randomWord);
        }
        return constructTitle(titleWords.toArray(new String[0]));
    }
    
    private static String constructTitle(String ... words) {
        return StringUtils.join(words, " ");
    }
    
    /** Private classes */
    
    private abstract static class Word {
        public abstract String getRandomWord();
    }
    
    private static class Determiner extends Word {
        /* Words may be repeated to increase probability of being selected */
        private static final String[] DETERMINERS = { "The", "The", "The", "The", "The", "A", "A",
            "An", "An", "My", "Our", "Their", "His", "Her", "The", "The"
        };
        
        private static final Determiner _instance = new Determiner();
        
        public static Determiner getInstance() {
            return _instance;
        }
        
        @Override
        public String getRandomWord() {
            Random random = new Random();
            int idx = random.nextInt(DETERMINERS.length);
            return DETERMINERS[idx];
        }
    }
    
    private static class Preposition extends Word {
        /* Words may be repeated to increase probability of being selected */
        private static final String[] PREPOSITIONS = { "In", "At", "On", "Over", "After", "To",
            "Of", "Under", "From", "Through", "Before"
        };
        
        private static final Preposition _instance = new Preposition();
        
        public static Preposition getInstance() {
            return _instance;
        }
        
        @Override
        public String getRandomWord() {
            Random random = new Random();
            int idx = random.nextInt(PREPOSITIONS.length);
            return PREPOSITIONS[idx];
        }
    }
    
    private static class Conjunction extends Word {
        /* Words may be repeated to increase probability of being selected */
        private static final String[] CONJUNCTIONS = { "for", "and", "and", "and", "but", "or",
            "and", "for", "and" };
        
        private static final Conjunction _instance = new Conjunction();
        
        public static Conjunction getInstance() {
            return _instance;
        }
        
        @Override
        public String getRandomWord() {
            Random random = new Random();
            int idx = random.nextInt(CONJUNCTIONS.length);
            return CONJUNCTIONS[idx];
        }
    }
    
    private static class Noun extends Word {
        private static final int CONSONANT_NOUNS_INDEX = 0;
        private static final int VOWEL_NOUNS_INDEX = 1;
        /* Words may be repeated to increase probability of being selected */
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
              "Mountain", "Hill", "Mission", "Dad", "Mom", "Brother", "Sister", "Husband", "Wife",
              "Soul", "Art", "Glass", "Heart", "Time", "Moon", "Sun", "Star", "Planet", "Jet",
              "Sky", "Fence", "Neighborhood", "Door", "Window", "Opportunity", "Salesman", "Force"
            },
            //starts with vowel
            { "Earth", "Idea", "It", "I", "Everything", "Eve", "Esther", "Alien", "Index",
              "Animal", "Elephant", "Albatross", "Ice", "Eagle", "Onyx", "Orange", "Apple",
              "Angel", "Angle", "Upstairs", "Understanding", "Overture", "Opening", "Ox",
              "Appointment", "Attitude", "Employee", "Eye", "Item", "Oregon", "Air", "Airplane",
              "Operation", "Office", "Uncle", "Aunt", "Ant", "Ape", "Ending", "End", "Exit",
              "Everyone", "Ether", "Airport", "Igloo", "Eskimo", "Iceland", "Alaska", "Italy",
              "Escape", "Immigrant", "Hour", "Advice", "Arrival"
            }
        };
        
        private static final Noun _instance = new Noun();
        
        public static Noun getInstance() {
            return _instance;
        }
        
        @Override
        public String getRandomWord() {
            Random random = new Random();
            int nounsIdx = random.nextInt(2);
            if (nounsIdx == CONSONANT_NOUNS_INDEX) {
                return getRandomConsonantNoun();
            }
            return getRandomVowelNoun();
        }
        
        public String getRandomConsonantNoun() {
            Random random = new Random();
            int idx = random.nextInt(NOUNS[CONSONANT_NOUNS_INDEX].length);
            return NOUNS[CONSONANT_NOUNS_INDEX][idx];
        }
        
        public String getRandomVowelNoun() {
            Random random = new Random();
            int idx = random.nextInt(NOUNS[VOWEL_NOUNS_INDEX].length);
            return NOUNS[VOWEL_NOUNS_INDEX][idx];
        }
    }
    
    private static class Verb extends Word {
        private static final int PRESENT_VERBS_INDEX = 0;
        private static final int PAST_VERBS_INDEX = 1;
        /* Words may be repeated to increase probability of being selected */
        private static final String[][] VERBS = {
            //present
            { "is", "Runs", "Goes", "Walks", "Says", "Touches", "Speaks", "Drifts", "Screams", "Wanders",
              "Hears", "Sees", "Believes", "Loves", "Stays", "Leaves", "Pays", "Leads", "Laughs", "Smiles",
              "Speaks", "Hurts", "Kills", "Saves", "Grows", "Drives", "Flies", "Swims", "Takes", "Loves",
              "is", "Writes", "Makes", "Cooks", "Sells", "Steals", "Falls", "Calls", "Travels", "is",
              "Loves", "is", "Sings", "Put", "Wonders", "Thinks", "Escapes", "Likes", "Likes", "Climbs",
              "Awakens", "Fight"
            },
            //past
            { "Was", "Ran", "Went", "Walked", "Said", "Touched", "Heard", "Saw", "Believed", "Loved",
              "Stayed", "Left", "Paid", "Led", "Laughed", "Smiled", "Fell", "Won", "Battled", "Lost",
              "Spoke", "Hurt", "Killed", "Saved", "Grew", "Drove", "Flew", "Swam", "Took", "Wrote", "Made",
              "Cooked", "Sold", "Sang", "Stole", "Fell", "Called", "Traveled", "Put", "Thought", "Escaped",
              "Liked", "Quit", "Died", "Drifted", "Floated", "Shot", "Arrived", "Banned", "Answered",
              "Cut", "Froze", "Taught", "Shut"
            }
        };
        
        private static final Verb _instance = new Verb();
        
        public static Verb getInstance() {
            return _instance;
        }
        
        @Override
        public String getRandomWord() {
            Random random = new Random();
            int nounsIdx = random.nextInt(2);
            if (nounsIdx == PRESENT_VERBS_INDEX) {
                return getRandomPresentVerb();
            }
            return getRandomPastVerb();
        }
        
        public String getRandomPresentVerb() {
            Random random = new Random();
            int idx = random.nextInt(VERBS[PRESENT_VERBS_INDEX].length);
            return VERBS[PRESENT_VERBS_INDEX][idx];
        }
        
        public String getRandomPastVerb() {
            Random random = new Random();
            int idx = random.nextInt(VERBS[PAST_VERBS_INDEX].length);
            return VERBS[PAST_VERBS_INDEX][idx];
        }
    }
    
    private static class Adjective extends Word {
        private static final int CONSONANT_ADJECTIVES_INDEX = 0;
        private static final int VOWEL_ADJECTIVES_INDEX = 1;
        /* Words may be repeated to increase probability of being selected */
        private static final String[][] ADJECTIVES = {
            //starts with consonant
            { "Beautiful", "Weird", "Heavy", "Loud", "Light", "Crazy", "Mean", "Wonderful",
              "Magical", "Scary", "Horrible", "Stellar", "Broken", "Funny", "Narrow", "Dry",
              "Big", "Small", "Wide", "Black", "White", "Red", "Blue", "Green", "Yellow",
              "Gray", "Brown", "Purple", "Dark", "Sharp", "Bloody", "High", "Low", "Terrible",
              "Lovely", "Nice", "Wild", "Hard", "Long", "Short", "Frozen", "Sad", "Sweet",
              "Bitter", "Clear", "Quiet"
            },
            //starts with vowel
            { "Incredile", "Amazing", "Easy", "Ugly", "Orange", "Inner", "Inside", "Impossible",
              "Indescribable", "Undisputed", "Inconvenient", "Unbroken", "Undefeated", "Adventurous",
              "Awesome", "Azure", "Ultimate", "Ultraviolet", "Unbearable", "Unbreakable", "Unchanged",
              "Uncomfortable", "Obtrusive", "Obtuse", "Obese", "Offensive", "Opaque", "Obnoxious",
              "Outstanding", "Open", "Accidental", "Official", "Omniscient", "Innocent", "Illegal"
            }
        };
        
        private static final Adjective _instance = new Adjective();
        
        public static Adjective getInstance() {
            return _instance;
        }
        
        @Override
        public String getRandomWord() {
            Random random = new Random();
            int nounsIdx = random.nextInt(2);
            if (nounsIdx == CONSONANT_ADJECTIVES_INDEX) {
                return getRandomConsonantAdjective();
            }
            return getRandomVowelAdjective();
        }
        
        public String getRandomConsonantAdjective() {
            Random random = new Random();
            int idx = random.nextInt(ADJECTIVES[CONSONANT_ADJECTIVES_INDEX].length);
            return ADJECTIVES[CONSONANT_ADJECTIVES_INDEX][idx];
        }
        
        public String getRandomVowelAdjective() {
            Random random = new Random();
            int idx = random.nextInt(ADJECTIVES[VOWEL_ADJECTIVES_INDEX].length);
            return ADJECTIVES[VOWEL_ADJECTIVES_INDEX][idx];
        }
    }
}
