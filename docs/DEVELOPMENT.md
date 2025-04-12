# Development Guidelines

## Code Formatting
IntelliJ IDEA, Eclipse and most other IDEs allow automatic code formatting. In order to keep merge conflicts to a
minimum and the code formatted the same for all developers, a coding style definition (`docs/code_style.xml`) is
provided in eclipse format. This file can be imported in Eclipse and IntelliJ IDEA.

## Release Process
New releases of VANESA are automatically built and created on GitHub using actions. To trigger a new release, the
following steps need to be performed:

1. Update the version number in the `pom.xml` following SemVer practices like `0.5` or `0.5_pre` for a pre-release
2. Update the version number in the `README.md`
3. Commit the changes of 1. and 2. and tag this commit with `v.{version}`, so version `0.5` is tagged `v.0.5`
4. Push the commit and tag
5. Wait for the action to complete
6. Update the release's description with the primary changes, required Java version, etc.
7. Update the version number in the gh-pages branch
