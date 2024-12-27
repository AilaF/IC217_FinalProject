package application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoodAnalyzer {

    public static final Map<String, List<String>> moodKeywords = new HashMap<>();

    static {
        moodKeywords.put("HAPPY", List.of(
        		"happy", "joy", "excited", "great", "fantastic", "love", "yay", "yey", "love", ":)", "excited"));
        
        moodKeywords.put("SAD", List.of(
        		"sad", "unhappy", "depressed", "down", "sorrow", "cry", "pain", ":("));
        
        moodKeywords.put("ANGRY", List.of(
        		"angry", "mad", "frustrated", "irritated", "rage", "argh!"));
        
        moodKeywords.put("ANXIOUS", List.of(
        		"anxious", "nervous", "worried", "stressed", "fear", "disappear"));
        
        moodKeywords.put("HATRED", List.of(""
        		+ "hate", "dislike", "despise", "disgust"));
    }

    public static Map<String, Integer> analyzeMood(String content) {
        Map<String, Integer> moodCount = new HashMap<>();
        String lowerContent = content.toLowerCase();

        for (Map.Entry<String, List<String>> entry : moodKeywords.entrySet()) {
            String mood = entry.getKey();
            List<String> keywords = entry.getValue();
            int count = 0;

            for (String keyword : keywords) {
                count += countOccurrences(lowerContent, keyword);
            }

            if (count > 0) {
                moodCount.put(mood, count);
            }
        }
        return moodCount;
    }

    private static int countOccurrences(String content, String keyword) {
        String[] words = content.split("\\W+");
        int count = 0;
        for (String word : words) {
            if (word.equalsIgnoreCase(keyword)) {
                count++;
            }
        }
        return count;
    }
}
