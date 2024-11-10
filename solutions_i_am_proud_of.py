# Apply ROT[key] to text.
def rotate(text: str, key: int) -> str:
    def offset(char: str) -> str:
        base = ord("a") if char.islower() else ord("A")
        return chr((ord(char) - base + key) % 26 + base)
        
    return "".join(offset(c) if c.isalpha() else c for c in text)


# Convert a number up to 3999 into Roman numerals.
def roman(num: int) -> str:
    def convert(n: int, pow_of_10: int) -> str:
        one, five, ten = ("IVX", "XLC", "CDM", "MMM")[pow_of_10]
        return (
            one if n % 5 == 4 else five if n > 4 else "") + (
            ten if n == 9 else five if n == 4 else one * (n % 5)
        )
    return "".join(
        convert(int(n), i)
        for n, i in zip(str(num), reversed(range(len(str(num)))))
    )


# Determine the relation of list one to list two.
EQUAL, SUBLIST, SUPERLIST, UNEQUAL = "EQL", "SUB", "SUP", "UNEQ"

def sublist(list_one, list_two) -> str: 
    a = "*".join(map(str, list_one)) + "*"
    b = "*".join(map(str, list_two)) + "*"
    return EQUAL if a == b else SUBLIST if a in b else SUPERLIST if b in a else UNEQUAL


# Check the validity of an ISBN.
def is_valid(isbn: str) -> bool:
    isbn = {
        i: 10 if (n == "X" and i == 1) else int(n) if n.isdigit() else 0.1
        for i, n in zip(range(10, -1, -1), isbn.replace("-", ""))
    }
    return len(isbn) == 10 and sum(i * n for i, n in isbn.items()) % 11 == 0


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


# Encrypt a string using the Atbash cipher, returning text in blocks of 5,
# keeping numerals unchanged and ignoring non-alphanumeric characters.
atbash = str.maketrans(abc := "abcdefghijklmnopqrstuvwxyz", abc[::-1], " ")

def encode(txt: str) -> str:
    ciph = "".join(
        c.lower().translate(atbash) for c in txt if c.isalnum()
    )
    return " ".join(ciph[i:i+5] for i in range(0, len(ciph), 5))


# Given two strings num1 and num2 representing non-negative integers,
# return their product as a string, converting only single digits at a time.
def multiply(self, num1: str, num2: str) -> str:
    def build(a: int, string: str) -> int:
        return sum(a * int(x) * 10**n for n, x in enumerate(string))

    def end(x: int) -> int:
        return 1 + sum(1 for n in range(len(num1 + num2)) if x // 10**n)

    product: int = build(
        a=build(a=1, string=reversed(num1)),
        string=reversed(num2)
    )
    digits: list[int] = [
        product % 10**n // 10 ** (n - 1) for n in range(1, max(end(product), 2))
    ]
    return "".join(str(d) for d in reversed(digits))
