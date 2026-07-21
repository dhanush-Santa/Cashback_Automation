package api.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.base.BaseAPITest;
import api.clients.SearchClient;

public class SearchTest extends BaseAPITest {

    private static final String COUNTRY_CODE = "US";

    private SearchClient searchClient;
    private List<String> storeNames;

    private String randomStoreName;
    private String randomStoreSearchText;

    private String longestStoreName;
    private String longestStoreSearchText;

    private String duplicateStoreName;
    private long duplicateStoreCount;
    private String duplicateStoreSearchText;

    @BeforeClass
    public void searchSetUp() {
        searchClient = new SearchClient();
        storeNames = searchClient.getStoreNames(COUNTRY_CODE);

        Assert.assertNotNull(storeNames, "Store list must not be null");
        Assert.assertFalse(storeNames.isEmpty(), "Store list must not be empty");

        List<String> searchableStoreNames = storeNames.stream()
                .filter(storeName -> !getTwoCharacterSearchTerms(storeName).isEmpty())
                .collect(Collectors.toList());

        Assert.assertFalse(
                searchableStoreNames.isEmpty(),
                "No store names contain a valid two-character search term"
        );

        randomStoreName = searchableStoreNames.get(
                new Random().nextInt(searchableStoreNames.size())
        );
        randomStoreSearchText = findSupportedSearchTerm(randomStoreName);

        longestStoreName = searchableStoreNames.stream()
                .max((first, second) -> Integer.compare(first.length(), second.length()))
                .orElseThrow(() -> new AssertionError("Unable to identify longest searchable store"));

        longestStoreSearchText = findSupportedSearchTerm(longestStoreName);

        Map<String, Long> storeNameCounts = storeNames.stream()
                .collect(Collectors.groupingBy(
                        this::normalize,
                        Collectors.counting()
                ));

        storeNameCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .findFirst()
                .ifPresent(entry -> {
                    duplicateStoreName = entry.getKey();
                    duplicateStoreCount = entry.getValue();
                });

        if (duplicateStoreName != null) {
            duplicateStoreSearchText = findSupportedSearchTerm(duplicateStoreName);
        }

        System.out.println("Random store selected: " + randomStoreName);
        System.out.println("Random store search text: " + randomStoreSearchText);
        System.out.println("Longest store name: " + longestStoreName);
        System.out.println("Longest store search text: " + longestStoreSearchText);

        if (duplicateStoreName != null) {
            System.out.println("Duplicate store detected: " + duplicateStoreName
                    + ", count: " + duplicateStoreCount);
        }
    }

    @Test(priority = 1)
    public void validateExactSearch() {
        validateSearchResults(randomStoreSearchText, randomStoreName);
    }

    @Test(priority = 2)
    public void validateLowerCaseSearch() {
        validateSearchResults(
                randomStoreSearchText.toLowerCase(Locale.ROOT),
                randomStoreName
        );
    }

    @Test(priority = 3)
    public void validateUpperCaseSearch() {
        validateSearchResults(
                randomStoreSearchText.toUpperCase(Locale.ROOT),
                randomStoreName
        );
    }

    @Test(priority = 4)
    public void validateCamelCaseSearch() {
        validateSearchResults(
                convertToCamelCase(randomStoreSearchText),
                randomStoreName
        );
    }

    @Test(priority = 5)
    public void validateTwoCharacterSearch() {
        Assert.assertEquals(
                randomStoreSearchText.length(),
                2,
                "Selected search text must contain exactly two characters"
        );

        validateSearchResults(randomStoreSearchText, randomStoreName);
    }

    @Test(priority = 6)
    public void validateLongestStoreNameSearch() {
        validateSearchResults(longestStoreSearchText, longestStoreName);
    }

    @Test(priority = 7)
    public void validateInvalidSearch() {
        String invalidSearch = "invalid-store-" + UUID.randomUUID();

        List<String> results = searchClient.searchStoreNames(COUNTRY_CODE, invalidSearch);

        Assert.assertTrue(
                results.isEmpty(),
                "An unknown search term must not return stores. Returned: " + results
        );
    }

    @Test(priority = 8)
    public void validateSpecialCharacterSearch() {
        List<String> results = searchClient.searchStoreNames(COUNTRY_CODE, "@#$%");

        Assert.assertTrue(
                results.isEmpty(),
                "Special-character-only search must not return stores. Returned: " + results
        );
    }

    @Test(priority = 9)
    public void validateSingleCharacterSearch() {
        List<String> results = searchClient.searchStoreNames(COUNTRY_CODE, "A");

        Assert.assertTrue(
                results.isEmpty(),
                "A one-character search must not return results because minimum length is two"
        );
    }

    @Test(priority = 10)
    public void validateEmptySearch() {
        List<String> results = searchClient.searchStoreNames(COUNTRY_CODE, "");

        Assert.assertTrue(
                results.isEmpty(),
                "An empty search must not return results"
        );
    }

    @Test(priority = 11)
    public void validateNullSearch() {
        List<String> results = searchClient.searchStoreNames(COUNTRY_CODE, null);

        Assert.assertTrue(
                results.isEmpty(),
                "A null search must not return results"
        );
    }

    @Test(priority = 12)
    public void validateSearchWithSpaces() {
        validateSearchResults(
                "  " + randomStoreSearchText + "  ",
                randomStoreName
        );
    }

    @Test(priority = 13)
    public void validateDuplicateStoreNameSearch() {
        if (duplicateStoreName == null) {
            System.out.println("No duplicate store names found; duplicate test skipped.");
            return;
        }

        List<String> results = searchClient.searchStoreNames(
                COUNTRY_CODE,
                duplicateStoreSearchText
        );

        long returnedDuplicateCount = results.stream()
                .filter(storeName -> normalize(storeName).equals(duplicateStoreName))
                .count();

        Assert.assertEquals(
                returnedDuplicateCount,
                duplicateStoreCount,
                "Search must return every duplicate occurrence of: " + duplicateStoreName
        );

        validateSearchResults(duplicateStoreSearchText, duplicateStoreName);
    }

    /**
     * Validates production search behavior:
     * - Search returns at least one result.
     * - Every result contains the search text, ignoring case.
     * - All expected matching brands are returned.
     * - No unrelated brands are returned.
     * - Duplicate brand occurrences are preserved.
     */
    private void validateSearchResults(String searchTerm, String expectedStoreName) {
        String normalizedSearchTerm = normalize(searchTerm);

        List<String> expectedResults = storeNames.stream()
                .filter(storeName -> normalize(storeName).contains(normalizedSearchTerm))
                .collect(Collectors.toList());

        List<String> actualResults = searchClient.searchStoreNames(COUNTRY_CODE, searchTerm);

        Assert.assertFalse(
                expectedResults.isEmpty(),
                "Test data has no stores matching search text: " + searchTerm
        );

        Assert.assertFalse(
                actualResults.isEmpty(),
                "Search returned no results for: " + searchTerm
        );

        Assert.assertTrue(
                actualResults.stream()
                        .allMatch(storeName ->
                                normalize(storeName).contains(normalizedSearchTerm)),
                "Every returned store must contain search text '" + searchTerm
                        + "'. Returned results: " + actualResults
        );

        Assert.assertTrue(
                actualResults.stream()
                        .anyMatch(storeName ->
                                normalize(storeName).equals(normalize(expectedStoreName))),
                "Expected store was not returned: " + expectedStoreName
        );

        Assert.assertEquals(
                toFrequencyMap(actualResults),
                toFrequencyMap(expectedResults),
                "Search results do not match the expected matching brands for: " + searchTerm
        );
    }

    /**
     * Finds a two-character term from the given store name that the API supports.
     * This avoids assuming that full names or multi-word names are searchable.
     */
    private String findSupportedSearchTerm(String storeName) {
        List<String> candidates = new ArrayList<>(getTwoCharacterSearchTerms(storeName));
        Collections.shuffle(candidates);

        for (String candidate : candidates) {
            List<String> expectedResults = storeNames.stream()
                    .filter(name -> normalize(name).contains(normalize(candidate)))
                    .collect(Collectors.toList());

            List<String> actualResults = searchClient.searchStoreNames(
                    COUNTRY_CODE,
                    candidate
            );

            if (!actualResults.isEmpty()
                    && toFrequencyMap(actualResults).equals(toFrequencyMap(expectedResults))) {
                return candidate;
            }
        }

        throw new AssertionError(
                "Could not find an API-supported two-character search term for store: "
                        + storeName
        );
    }

    private Set<String> getTwoCharacterSearchTerms(String storeName) {
        Set<String> searchTerms = new LinkedHashSet<>();

        if (storeName == null) {
            return searchTerms;
        }

        String trimmedStoreName = storeName.trim();

        for (int index = 0; index < trimmedStoreName.length() - 1; index++) {
            char firstCharacter = trimmedStoreName.charAt(index);
            char secondCharacter = trimmedStoreName.charAt(index + 1);

            if (isAsciiLetterOrDigit(firstCharacter)
                    && isAsciiLetterOrDigit(secondCharacter)) {
                searchTerms.add(
                        ("" + firstCharacter + secondCharacter).toLowerCase(Locale.ROOT)
                );
            }
        }

        return searchTerms;
    }

    private boolean isAsciiLetterOrDigit(char character) {
        return (character >= 'A' && character <= 'Z')
                || (character >= 'a' && character <= 'z')
                || (character >= '0' && character <= '9');
    }

    private Map<String, Long> toFrequencyMap(List<String> names) {
        return names.stream()
                .collect(Collectors.groupingBy(
                        this::normalize,
                        Collectors.counting()
                ));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String convertToCamelCase(String value) {
        StringBuilder result = new StringBuilder();
        boolean upperCase = false;

        for (char character : value.toCharArray()) {
            if (Character.isLetter(character)) {
                result.append(
                        upperCase
                                ? Character.toUpperCase(character)
                                : Character.toLowerCase(character)
                );
                upperCase = !upperCase;
            } else {
                result.append(character);
            }
        }

        return result.toString();
    }
}