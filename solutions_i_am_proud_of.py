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
def is_valid(isbn):
    isbn_cleaned = tuple(
        isbn.replace("-", "").rstrip("X") + ("#" if isbn[-1:] == "X" else "")
    )
    isbn_digits = tuple(
        map(
            lambda n: int(n) if n.isdigit() else 10 if n == "#" else 0.1,
            isbn_cleaned
        )
    )
    checksum = sum(
        map(
            lambda n, i: n * i,
            isbn_digits,
            range(10, 0, -1)
        )
    )
    return len(isbn_digits) == 10 and checksum % 11 == 0
