# Expression Syntax

Several Petri net elements allow setting functions for properties which are evaluated during simulation.

Evaluation of functions uses `US` locale.

## Properties and Place Tokens

Places, transitions, and arcs allow custom properties to be defined for use in function evaluation and parametrized
simulation. Properties are locally scoped to functions of the same entity and used by their case-sensitive name.

**Example:** Arc `p1` defines the property `x = 12` and the arc function `4 * x`. During simulation, this arc is
evaluated and will destroy/create `48` tokens.

## Available Operators

| Name       | Description           |
|------------|-----------------------|
| `+x`       | Prefix positive       |
| `-x`       | Prefix negative       |
| `x + y`    | Addition              |
| `x - y`    | Subtraction           |
| `x * y`    | Multiplication        |
| `x / y`    | Division              |
| `x ^ y`    | Power of              |
| `x % y`    | Modulo                |
| `x == y`   | Equality              |
| `x != y`   | Inequality            |
| `x > y`    | Greater than          |
| `x >= y`   | Greater than or equal |
| `x < y`    | Less than             |
| `x <= y`   | Less than or equal    |
| `x && y`   | Logical AND           |
| `x \|\| y` | Logical OR            |

## Available Functions

| Name           | Description                  |
|----------------|------------------------------|
| abs            |                              |
| `acos(x)`      | Arc cosine of `x` (degrees)  |
| acosh          |                              |
| acot           |                              |
| acoth          |                              |
| and            | Logical AND                  |
| `asin(x)`      | Arc sine of `x` (degrees)    |
| asinh          |                              |
| `atan(x)`      | Arc tangent of `x` (degrees) |
| atan2          |                              |
| atanh          |                              |
| bn             | Bernoulli                    |
| ceil / ceiling |                              |
| `cos(x)`       | Cosine of `x` (degrees)      |
| cosh           |                              |
| cot            |                              |
| coth           |                              |
| csc            |                              |
| csch           |                              |
| deg            |                              |
| e              | e constant                   |
| exp            | $e^x$                        |
| fact           | Factorial                    |
| floor          |                              |
| fractionalpart |                              |
| gamma          |                              |
| if             |                              |
| integralpart   |                              |
| log            |                              |
| log10          |                              |
| log2           |                              |
| max            |                              |
| min            |                              |
| not            | Logical NOT                  |
| or             | Logical OR                   |
| pi             | PI constant                  |
| rad            |                              |
| reciprocal     |                              |
| root           |                              |
| round          |                              |
| sec            |                              |
| sech           |                              |
| `sin(x)`       | Sine of `x` (degrees)        |
| sinh           |                              |
| sqrt           |                              |
| `tan(x)`       | Tangent of `x` (degrees)     |
| tanh           |                              |
