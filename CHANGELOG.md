# Change Log
All notable changes to this project will be documented in this file.

## [3.4.3] - 2021-06-03

### Added
- Lombok added

## [3.4.2] - 2021-05-21

### Changed
- Return null for nullable nested values

## [3.4] - 2021-05-21

### Added
- ReportEditor javadoc

## [3.4] - 2021-05-20

### Added
- ReportEditor service

## [3.3.1] - 2021-05-19

### Fixed
- ReportLoaderResult.getResultWithErrors and ReportLoaderResult.getResultWithoutErrors NullPointerException

## [3.3] - 2021-05-19

### Added
- Nested column value.
- Column and cell translation

### Fixed
- ReportDataReader.getCellValue get row if row index is null (last row) or negative

## [3.2.10] - 2021-05-18

### Changed
- Tests folder moved to mail package

## [3.2.9] - 2021-05-18

### Added
- displayZeros method added to Configration annotation
### Fixed
- AnnotationUtils convert ColumnGetter to Column

## [3.2.8] - 2021-05-18

### Added
- ReportLoader.getCellValue more data types support
- GREPORTS_TEST_DIR environment variable to can run the tests
### Changed
- Make AbstractCellValidator and AbstractColumnValidator constructors public

## [3.2.7] - 2021-05-13

### Added
- ReportLoaderResult.getResultWithErrors added.
### Changed
- Changed ReportLoaderResult.getResultWithoutErrors method logic

## [3.2.6] - 2021-05-12

### Added
- ReportLoaderResult getResultMap added.

## [3.2.5] - 2021-05-11

### Added
- ReportLoaderResult. Added getResultWithoutErrors method.
- NotEmptyValidator added.
### Changed
- Translator. The logic of method Translator.translate was migrated to MessageFormat.format method

## [3.2.4] - 2021-04-21

### Fixed
- Fixed getConfigurator method.

## [3.2.2] - 2021-03-23

### Added
- Javadoc for some classes.
### Changed
- Changed Apache POI version in MANIFEST.MF
- Some small code improvements.
### Fixed
- Fixed the obtaining of the result changer.