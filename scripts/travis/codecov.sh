#!/bin/bash

set -e

# shellcheck source=travis_retry.sh
source "${TRAVIS_BUILD_DIR}/scripts/travis/travis_retry.sh"

if [[ "${COVERAGE_BUILD}" -ne 0 ]]; then
  echo "Sending coverage data to Codecov"
  travis_retry codecov --required --token "${CODECOV_TOKEN}" --root "${TRAVIS_BUILD_DIR}" -X gcov;
fi
