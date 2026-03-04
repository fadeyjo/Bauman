def decision_condition_coverage():
    tests = set(statement_coverage())
    tests.update(decision_coverage())
    tests.update(condition_coverage())
    return list(tests)