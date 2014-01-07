package org.jsystemtest.systemobjects.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CollectionMatcher<E> extends BaseMatcher<List<E>> {

	final protected List<E> expected;
	private List<E> singlesInActual, singlesInExpected;
	protected boolean allItems = false;
	private List<Comparator<E>> comparators;

	StringBuilder report = new StringBuilder();

	public CollectionMatcher(List<E> expected) {
		this.expected = expected.subList(0, expected.size());
	}

	public Matcher<List<E>> containsAllItems() {
		this.allItems = true;
		return this;
	}

	public boolean matches(final List<E> actual) {

		singlesInExpected = new ArrayList<E>(expected.size());
		singlesInActual = new ArrayList<E>(actual.size());

		if (!allItems) {
			if (expected.size() > actual.size()) {
				report.append("Size of actual items is ").append(
						actual.size());
				report.append(", should be at least ").append(
						expected.size());
				return false;
			}
			singlesInActual.addAll(actual);
		} else {
			if (expected.size() != actual.size()) {
				report.append("Size of actual items is ").append(
						actual.size());
				report.append("instead of ").append(expected.size());
				return false;
			}
		}
		Comparator<E> comparator;
		if (this.comparators == null) {
			comparator = simpleComparator;
		} else {
			comparator = new CombinedComparator<E>(comparators);
		}
		List<E> sortedActual = actual.subList(0, actual.size());
		List<E> sortedExpected = expected.subList(0, expected.size());
		Collections.sort(sortedActual, comparator);
		Collections.sort(sortedExpected, comparator);
		for (int iactual = 0, iexpected = 0; (iactual < sortedActual.size() || !allItems)
				&& (iexpected < sortedExpected.size());) {
			if (iactual >= sortedActual.size()) {// when atLeast flag is true,
													// all expected items which
													// had no actual - must be
													// added to the singles
													// list.
				singlesInExpected.addAll(sortedExpected.subList(iexpected,
						sortedExpected.size()));
				break;
			}
			E currActual = sortedActual.get(iactual);
			E currExpected = sortedExpected.get(iexpected);

			int comparison = comparator.compare(currActual, currExpected);
			if (0 == comparison) {
				// "Found " + currActual.toString());

				// matches.add(Pair.with(currExpected, currActual));
				singlesInActual.remove(currActual);
				iexpected++;
				iactual++;
			} else if (comparison > 0) {
				singlesInExpected.add(currExpected);
				iexpected++;
			} else {
				singlesInActual.add(currActual);
				iactual++;
			}
		}
		if (report != null) {
			for (E e : singlesInExpected) {
				report.append(e.toString()).append(
						" was expected but not found");
				report.append("\n");
			}

			for (E e : singlesInActual) {
				report.append(e.toString()).append(" was found");
				if (!allItems) {
					report.append(" unexpectedly");
				}
				report.append("\n");
			}

		}

		return singlesInExpected.isEmpty()
				&& (singlesInActual.isEmpty() || !allItems);
	}

	public void describeTo(Description description) {
		if (allItems){			
			description.appendText("all ");
		}
		description.appendText("items to exist in: ");
		description.appendValueList("{", ", ", "}", expected);
		description.appendText("\n").appendText(report.toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(Object item) {
		if (item instanceof List) {
			return matches((List<E>) item);
		}
		return false;
	}

	public CollectionMatcher<E> addComparator(Comparator<E> comparator) {
		if (this.comparators == null) {
			comparators = new ArrayList<Comparator<E>>();
		}
		comparators.add(comparator);
		return this;
	}

	protected final Comparator<E> simpleComparator = new Comparator<E>() {

		@Override
		public int compare(E o1, E o2) {
			if (o1 instanceof Comparable<?>) {
				@SuppressWarnings("unchecked")
				Comparable<E> o1a = (Comparable<E>) o1;
				return o1a.compareTo(o2);
			} else {
				if (o1 == null) {
					if (o2 == null)
						return 0;
					return -1;
				} else if (o2 == null)
					return 1;
				return Integer.compare(o1.hashCode(), o2.hashCode());
			}
		}
	};
}
