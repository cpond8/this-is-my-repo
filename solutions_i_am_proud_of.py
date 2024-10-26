# Reverse a string of text.
def reverse(text):
    return "" if not len(text) else text[-1] + reverse(text[:-1])


# Apply ROT[key] to text.
def rotate(text, key):
    def offset(char):
        base = ord("a") if char.islower() else ord("A")
        return chr((ord(char) - base + key) % 26 + base)

    return "".join(map(lambda c: offset(c) if c.isalpha() else c, text))


# Convert a number up to 3999 into Roman numerals.
def roman(number):
    def convert_to_roman(n, power_of_ten):
        one, five, ten = ("IVX", "XLC", "CDM", "MMM")[power_of_ten]
        return (one if n % 5 == 4 else five if n > 4 else "") + (
            ten if n == 9 else five if n == 4 else one * (n % 5)
        )

    return "".join(
        map(
            convert_to_roman,
            [int(n) for n in str(number)],
            reversed(range(len(str(number))))
        )
    )


# Determine the relation of list one to list two.
EQUAL, SUBLIST, SUPERLIST, UNEQUAL = "EQL", "SUB", "SUP", "UNEQ"

def sublist(list_one, list_two):
    a = "*".join(map(str, list_one)) + "*"
    b = "*".join(map(str, list_two)) + "*"
    return EQUAL if a == b else SUBLIST if a in b else SUPERLIST if b in a else UNEQUAL


# Check the validity of an ISBN.
def is_valid(isbn_raw):
    def clean(isbn):
        return isbn.replace("-", "").rstrip("X") + ("#" if isbn[-1:] == "X" else "")
    def to_int(n):
        return int(n) if n.isdigit() else 10 if n == "#" else 0.1
    def checksum(isbn):
        return sum(map(lambda n, i: n * i, isbn, range(10, 0, -1)))

    isbn_int = [to_int(n) for n in clean(isbn_raw)]
    return len(isbn_int) == 10 and checksum(isbn_int) % 11 == 0


# Given an array of integers nums, return the length of
# the longest consecutive sequence of elements.
def longestConsecutive(self, nums):
    def sequence(prev, length=1):
        return length if prev + 1 not in nums else sequence(prev + 1, length + 1)

    return 0 if not nums else max(map(sequence, nums))


# Convert mi to km using the golden ratio for aesthetically superior results.
def miles_to_golden_km(miles: int) -> int:
    kilometers: int = 0
    while miles:
        mi, km = 0, 1
        while km <= miles:
            mi, km = km, (mi + km)
        kilometers += km
        miles -= mi
    return kilometers
