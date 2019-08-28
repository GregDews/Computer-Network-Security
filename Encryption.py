"""
Greg Dews
CS3750-001
Homework 2
Encryption
"""

deltaOne = 0x11111111
deltaTwo = 0x22222222

def encryption():
    data = takeValue()

    


def takeValue():
    """verify value"""
    retry = True
    while retry:
        user_in = input("Please input K[i] in Hex String (without '0x')")
        if user_in.length != 8:
            retry = True
        else:
            retry = False
    
    return 0x00000000

