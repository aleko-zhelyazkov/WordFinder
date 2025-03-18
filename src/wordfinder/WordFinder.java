package wordfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordFinder {

	private static final Integer MAX_LETTERS = 9;
	private static final String DICTIONARY_URL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";
	private static final List<String> SINGLE_LETTERED_WORDS = List.of("I", "A");
	
	private Map<Integer, List<String>> groomedWords = new HashMap<>();

	public List<String> loadAllWords() throws IOException, URISyntaxException {
		URL wordsUrl = new URI(DICTIONARY_URL).toURL();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(wordsUrl.openConnection().getInputStream()))) {
			return new ArrayList<>(br.lines().skip(2).toList());
		}
	}

	public List<String> findValidWords(List<String> allValidWords) {
		groomWords(allValidWords);
		return getNLetterWords(MAX_LETTERS).stream()
				.filter(this::isValidWord)
				.toList();
	}	
	
	private void groomWords(List<String> allValidWords) {
		allValidWords.stream()
			.filter(word -> word.length() <= MAX_LETTERS && SINGLE_LETTERED_WORDS.stream().anyMatch(word::contains))
			.forEach(word -> groomedWords.computeIfAbsent(word.length(), k -> new ArrayList<>())
					.add(word));
	}


	private List<String> getNLetterWords(int n) {
		return groomedWords.getOrDefault(n, new ArrayList<String>());
	}

	private boolean isValidWord(String word) {
		if (word.length() == 1) {
			return SINGLE_LETTERED_WORDS.contains(word); 
		}
		List<String> validWords = getNLetterWords(word.length());
		if (!validWords.contains(word)) {
			return false;
		}
		for (int i = 0; i < word.length(); i++) {
			String subWord = word.substring(0, i) + word.substring(i + 1);
			if (isValidWord(subWord)) {
				return true;
			}
		}
		if (word.length() < MAX_LETTERS) {
			validWords.remove(word);
		}
		return false;
	}

	public static void main(String[] args) {
		WordFinder wordFinder = new WordFinder();
		try {
			List<String> allValidWords = wordFinder.loadAllWords();
			System.out.println("Number of all words in dictionary: " + allValidWords.size());
			long startTime = System.nanoTime();
			List<String> result = wordFinder.findValidWords(allValidWords);
			long endTime = System.nanoTime();
			
			System.out.println("Execution time: " + ((endTime - startTime) / 1_000_000) + " milliseconds");
			System.out.println("Valid words count: " + result.size());
			String validResultWord = "STARTLING";
			System.out.println("Known valid word '" + validResultWord +"' exists in result: " + result.contains(validResultWord));
		
		} catch (IOException | URISyntaxException e) {
			System.out.println("Error loading words: " + e.getMessage());
		}
	}

}
