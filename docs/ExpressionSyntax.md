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

## Available Constants

| Name    | Description   |
|---------|---------------|
| `e`     | e constant    |
| `pi`    | PI constant   |
| `true`  | Logical true  |
| `false` | Logical false |

## Available Functions

| Name                                   | Description                                                          |
|----------------------------------------|----------------------------------------------------------------------|
| `abs(x)`                               | Absolute value of `x`                                                |
| `acos(x)`                              | Arc cosine of `x` (degrees)                                          |
| `acosh(x)`                             | Hyperbolic arc cosine of `x`                                         |
| `acot(x)`                              | Arc cotangent of `x` (degrees)                                       |
| `acoth(x)`                             | Hyperbolic arc cotangent of `x`                                      |
| `and(a, b)`                            | Logical AND of `a` and `b`                                           |
| `asin(x)`                              | Arc sine of `x` (degrees)                                            |
| `asinh(x)`                             | Hyperbolic arc sine of `x`                                           |
| `atan(x)`                              | Arc tangent of `x` (degrees)                                         |
| `atan2(y, x)`                          | Four quadrant inverse tangent of `y` and `x` (degrees)               |
| `atanh(x)`                             | Hyperbolic arc tangent of `x`                                        |
| `ceil(x)` / `ceiling(x)`               | Ceiling of `x`, least integer greater than or equal to `x`           |
| `cos(x)`                               | Cosine of `x` (degrees)                                              |
| `cosh(x)`                              | Hyperbolic cosine of `x`                                             |
| `cot(x)`                               | Cotangent of `x` (degrees)                                           |
| `coth(x)`                              | Hyperbolic cotangent of `x`                                          |
| `csc(x)`                               | Cosecant of angle `x` (degrees)                                      |
| `csch(x)`                              | Hyperbolic cosecant of `x`                                           |
| `deg(x)`                               | Convert `x` from radians to degrees                                  |
| `exp(x)`                               | $e^x$                                                                |
| `fact(x)`                              | Factorial `x!` of `x`                                                |
| `floor(x)`                             | Floor of `x`, greatest integer less than or equal to `x`             |
| `fractionalpart(x)`                    | Fractional part of `x`                                               |
| `gamma(x)`                             | Gamma function of `x`                                                |
| `if(condition, trueValue, falseValue)` | `trueValue` if `condition` evaluates to true, otherwise `falseValue` |
| `integralpart(x)`                      | Integral part of `x`                                                 |
| `log(x)`                               | Logarithm to base `e`                                                |
| `log10(x)`                             | Logarithm to base `10`                                               |
| `log2(x)`                              | Logarithm to base `2`                                                |
| `max(a, b, c, ...)`                    | Max value of the provided values                                     |
| `min(a, b, c, ...)`                    | Min value of the provided values                                     |
| `not(x)`                               | Logical NOT of `x`                                                   |
| `or(a, b)`                             | Logical OR of `a` and `b`                                            |
| `rad(x)`                               | Convert `x` from degrees to radians                                  |
| `reciprocal(x)`                        | Reciprocal `1/x` of `x`                                              |
| `root(x, n)`                           | `n`th root of `x`                                                    |
| `round(x[, precision])`                | Round `x` with `precision` decimal places (default: 0)               |
| `sec(x)`                               | Secant of angle `x` (degrees)                                        |
| `sech(x)`                              | Hyperbolic secant of `x`                                             |
| `sin(x)`                               | Sine of `x` (degrees)                                                |
| `sinh(x)`                              | Hyperbolic sine of `x`                                               |
| `sqrt(x)`                              | Square root of `x`                                                   |
| `tan(x)`                               | Tangent of `x` (degrees)                                             |
| `tanh(x)`                              | Hyperbolic tangent of `x`                                            |
