# Reverse a string of text.
def reverse(text: str) -> str:
    return "" if not text else text[-1] + reverse(text[:-1])


# Apply ROT[key] to text.
def rotate(text: str, key: int) -> str:
    def offset(char: str) -> str:
        base = ord("a") if char.islower() else ord("A")
        return chr((ord(char) - base + key) % 26 + base)
        
    return "".join([offset(c) if c.isalpha() else c for c in text])


# Convert a number up to 3999 into Roman numerals.
def roman(num: int) -> str:
    def convert(n: int, power_of_ten: int) -> str:
        one, five, ten = ("IVX", "XLC", "CDM", "MMM")[power_of_ten]
        return (one if n % 5 == 4 else five if n > 4 else "") + (
            ten if n == 9 else five if n == 4 else one * (n % 5)
        )

    return "".join(
        map(convert,
            [int(n) for n in str(num)],
            range(len(str(num)))[::-1])
    )


# Determine the relation of list one to list two.
EQUAL, SUBLIST, SUPERLIST, UNEQUAL = "EQL", "SUB", "SUP", "UNEQ"

def sublist(list_one, list_two) -> str: 
    a = "*".join(map(str, list_one)) + "*"
    b = "*".join(map(str, list_two)) + "*"
    return EQUAL if a == b else SUBLIST if a in b else SUPERLIST if b in a else UNEQUAL


# Check the validity of an ISBN.
def is_valid(isbn_raw: str) -> bool:
    def clean(isbn: str) -> str:
        return isbn.replace("-", "").rstrip("X") + ("#" if isbn[-1:] == "X" else "")
        
    def to_int(n: str) -> int:
        return int(n) if n.isdigit() else 10 if n == "#" else 0.1
        
    def checksum(isbn: list[int]) -> int:
        return sum([i * n for i, n in zip(isbn, range(10, 0, -1))])

    isbn_int = [to_int(n) for n in clean(isbn_raw)]
    return len(isbn_int) == 10 and checksum(isbn_int) % 11 == 0


# Given an array of integers nums, return the length of
# the longest consecutive sequence of elements.
def longestConsecutive(nums: list[int]) -> int:
    def sequence(prev: int, length: int=1) -> int:
        return (length
                if prev + 1 not in nums else
                sequence(prev + 1, length + 1))

    return 0 if not nums else max([sequence(n) for n in nums])


# Convert mi to km using the golden ratio for aesthetically superior results.
def miles_to_golden_km(miles: int) -> int:
    kilometers = 0
    while miles:
        mi, km = 0, 1
        while km <= miles:
            mi, km = km, (mi + km)
        kilometers += km
        miles -= mi
    return kilometers


# Given an m x n matrix of integers as a list of row vectors, return a list of every element in the matrix in spiral order.
def spiral_order(matrix: list[list[int]]) -> list[int]:
    x0, y0 = 0, 0
    xf = len(matrix[0]) - 1
    yf = len(matrix) - 1
    spiral = []

    while x0 <= xf and y0 <= yf:
        # top side rightward
        for x in range(x0, xf + 1):
            spiral.append(matrix[y0][x])
        y0 += 1
        # right side downward
        for y in range(y0, yf + 1):
            spiral.append(matrix[y][xf])
        xf -= 1
        # bottom side leftward
        if y0 <= yf:
            for x in range(xf, x0 - 1, -1):
                spiral.append(matrix[yf][x])
            yf -= 1
        # left side upward
        if x0 <= xf:
            for y in range(yf, y0 - 1, -1):
                spiral.append(matrix[y][x0])
            x0 += 1

    return spiral
