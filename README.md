# Two-pass-assembler-using-JAVA
A simple java program that can convert assembly program to machine code

# *Note*
1. Labels must be alphabets only.
2. No extra space allowed.

# sample assembly code
START 200
MOVER AREG,='5'
MOVEM AREG,X
L MOVER BREG,='2'
ORIGIN L+3
LTORG
NEXT ADD AREG,='1'
SUB BREG,='2'
BC LT,BACK
LTORG
BACK EQU L
ORIGIN NEXT+5
MULT CREG,='4'
STOP
X DS 1
END
