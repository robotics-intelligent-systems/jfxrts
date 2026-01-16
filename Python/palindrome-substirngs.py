def count_palindromic_substrings(s):
    # Function to count palindromes centered at left and right
    def expand_around_center(s, left, right):
        count = 0
        while left >= 0 and right < len(s) and s[left] == s[right]:
            count += 1
            left -= 1
            right += 1
        return count

    total_count = 0
    for i in range(len(s)):
        # Odd length palindromes (single character center)
        total_count += expand_around_center(s, i, i)
        # Even length palindromes (two character center)
        total_count += expand_around_center(s, i, i + 1)

    return total_count

# Example usage
input_string = "abbcbc"
print(count_palindromic_substrings(input_string))  # Output: 9

try:
    assert count_palindromic_substrings("") == 0
    assert count_palindromic_substrings("a") == 1
    assert count_palindromic_substrings("aa") == 3
    assert count_palindromic_substrings("abc") == 3
    assert count_palindromic_substrings("aaa") == 6
    assert count_palindromic_substrings("abbcbc") == 9
    print(f"[ASSERTION PASS]")
except AssertionError as e:
    print(f"[ASSERTION FAILED] {e}")
