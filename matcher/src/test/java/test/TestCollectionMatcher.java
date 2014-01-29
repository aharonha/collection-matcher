package test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.core.IsNot.not;

import org.jsystemtest.systemobjects.matcher.CollectionMatcher;
import org.junit.*;

public class TestCollectionMatcher{
	@Test
	public void testOneItem() {
		List<Integer> expected = new ArrayList<Integer>();
		List<Integer> actualList = new ArrayList<Integer>();
		expected.add(1);
		actualList.add(1);
		Assert.assertThat("{1} contain all of {1}", actualList,
				new CollectionMatcher<Integer>(expected).containsAllItems());

	}

	@Test
	public void testContained() {
		List<Integer> expected = new ArrayList<Integer>();
		List<Integer> actualList = new ArrayList<Integer>();
		expected.add(1);
		actualList.add(1);
		actualList.add(2);

		Assert.assertThat(
				"{1,2} is not contained in {1}",
				actualList,
				not(new CollectionMatcher<Integer>(expected).containsAllItems()));
	}

	@Test
	public void testStrings() {
		List<String> expected = new ArrayList<String>();
		List<String> actualList = new ArrayList<String>();
		expected.add("1");
		actualList.add("1");
		actualList.add("2");

		Assert.assertThat("{1} contained in {1,2}", actualList,
				new CollectionMatcher<String>(expected));
	}

	private static class A implements Comparable<A> {

		final int i;
		final String x;

		public A(int i, String x) {
			super();
			this.i = i;
			this.x = x;
		}

		@Override
		public int compareTo(A o) {
			return Integer.compare(i, o.i);
		}

		@Override
		public String toString() {
			return "A [i=" + i + ", x=" + x + "]";
		}

	}

	@Test
	public void testSelfComparableObject() {
		List<A> expected = new ArrayList<TestCollectionMatcher.A>();
		List<A> actualList = new ArrayList<TestCollectionMatcher.A>();
		expected.add(new A(1, "asdf"));

		actualList.add(new A(1, "1"));
		actualList.add(new A(2, "2"));

		Assert.assertThat("Self Comparable Object - ignored field x",
				actualList, new CollectionMatcher<A>(expected));
	}

	@Test
	public void testSelfComparableObjectWithComparator() {
		List<A> expected = new ArrayList<TestCollectionMatcher.A>();
		List<A> actualList = new ArrayList<TestCollectionMatcher.A>();
		expected.add(new A(1, "asdf"));

		actualList.add(new A(1, "asdf"));
		actualList.add(new A(2, "2"));

		Assert.assertThat(
				"Self Comparable Object With Comparator",
				actualList,
				new CollectionMatcher<A>(expected)
						.addComparator(new Comparator<TestCollectionMatcher.A>() {

							@Override
							public int compare(A o1, A o2) {
								int comparisonResult = o1.compareTo(o2);
								if (comparisonResult == 0) {
									comparisonResult = o1.x.compareTo(o2.x);
								}
								return comparisonResult;
							}
						}));
	}

	@Test
	public void testSelfComparableObjectWithComparatorItemNotFound() {
		List<A> expected = new ArrayList<TestCollectionMatcher.A>();
		List<A> actualList = new ArrayList<TestCollectionMatcher.A>();
		expected.add(new A(1, "asdf"));

		actualList.add(new A(2, "2"));
		actualList.add(new A(1, "ax"));

		Assert.assertThat(
				"Self Comparable Object With Comparator",
				actualList,
				not(new CollectionMatcher<A>(expected)
						.addComparator(new Comparator<TestCollectionMatcher.A>() {

							@Override
							public int compare(A o1, A o2) {
								int comparisonResult = o1.compareTo(o2);
								if (comparisonResult == 0) {
									comparisonResult = o1.x.compareTo(o2.x);
								}
								return comparisonResult;
							}
						})));
	}

	@Test
	public void testSelfComparableObjectWithCompositeComparator() {
		List<A> expected = new ArrayList<TestCollectionMatcher.A>();
		List<A> actualList = new ArrayList<TestCollectionMatcher.A>();
		expected.add(new A(1, "asdf"));

		actualList.add(new A(1, "asdf"));
		actualList.add(new A(2, "2"));

		Assert.assertThat(
				"Self Comparable Object With Composite Comparator",
				actualList,
				new CollectionMatcher<A>(expected).addComparator(
						new Comparator<TestCollectionMatcher.A>() {
							@Override
							public int compare(A o1, A o2) {
								return Integer.compare(o1.i	, o2.i);
							}
						}).addComparator(
						new Comparator<TestCollectionMatcher.A>() {
							@Override
							public int compare(A o1, A o2) {
								return o1.x.compareTo(o2.x);
							}
						}));
	}

	@Test
	public void testSelfComparableObjectWithCompositeComparatorItemNotFound() {
		List<A> expected = new ArrayList<TestCollectionMatcher.A>();
		List<A> actualList = new ArrayList<TestCollectionMatcher.A>();
		expected.add(new A(1, "asdf"));

		actualList.add(new A(2, "2"));
		actualList.add(new A(1, "ax"));

		Assert.assertThat(
				"Self Comparable Object With Composite Comparator",
				actualList,
				not(new CollectionMatcher<A>(expected).addComparator(
						new Comparator<TestCollectionMatcher.A>() {
							@Override
							public int compare(A o1, A o2) {
								return Integer.compare(o1.i	, o2.i);
							}
						}).addComparator(
						new Comparator<TestCollectionMatcher.A>() {
							@Override
							public int compare(A o1, A o2) {
								return o1.x.compareTo(o2.x);
							}
						})));
	}

	private static class B {

		final int i;
		final String x;

		public B(int i, String x) {
			this.i = i;
			this.x = x;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + i;
			result = prime * result + ((x == null) ? 0 : x.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			B other = (B) obj;
			if (i != other.i)
				return false;
			if (x == null) {
				if (other.x != null)
					return false;
			} else if (!x.equals(other.x))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "B [i=" + i + ", x=" + x + "]";
		}
	}

	//

	@Test
	public void testHashedObjectWithNoComparator() {
		List<B> expected = new ArrayList<TestCollectionMatcher.B>();
		List<B> actualList = new ArrayList<TestCollectionMatcher.B>();
		expected.add(new B(1, "asdf"));

		actualList.add(new B(2, "2"));
		actualList.add(new B(1, "asdf"));

		Assert.assertThat("Hashed Object With no Comparator", actualList,
				new CollectionMatcher<B>(expected));
	}

	@Test
	public void testHashedObjectWithNoComparatorItemNotFound() {
		List<B> expected = new ArrayList<TestCollectionMatcher.B>();
		List<B> actualList = new ArrayList<TestCollectionMatcher.B>();
		expected.add(new B(1, "asdf"));

		actualList.add(new B(2, "2"));
		actualList.add(new B(1, "ax"));

		Assert.assertThat("Hashed Object With no Comparator", actualList,
				not(new CollectionMatcher<B>(expected)));
	}

	
	@Test
	public void testHashedObjectWithComparator() {
		List<B> expected = new ArrayList<TestCollectionMatcher.B>();
		List<B> actualList = new ArrayList<TestCollectionMatcher.B>();
		expected.add(new B(1, "asdf"));

		actualList.add(new B(2, "2"));
		actualList.add(new B(1, "asdf"));

		Assert.assertThat("Hashed Object With composed Comparators", actualList,
				new CollectionMatcher<B>(expected).addComparator(new Comparator<TestCollectionMatcher.B>() {
					
					@Override
					public int compare(B o1, B o2) {						
						return Integer.compare(o1.i, o2.i);
					}
				}).addComparator(new Comparator<TestCollectionMatcher.B>() {
					
					@Override
					public int compare(B o1, B o2) {
						return o1.x.compareTo(o2.x);
					}
				}));
	}

	@Test
	public void testHashedObjectWithCompositeComparatorItemNotFound() {
		List<B> expected = new ArrayList<TestCollectionMatcher.B>();
		List<B> actualList = new ArrayList<TestCollectionMatcher.B>();
		expected.add(new B(1, "asdf"));

		actualList.add(new B(2, "2"));
		actualList.add(new B(1, "ax"));

		Assert.assertThat("Hashed Object With no Comparator", actualList,
				not(new CollectionMatcher<B>(expected).addComparator(new Comparator<TestCollectionMatcher.B>() {
					
					@Override
					public int compare(B o1, B o2) {						
						return Integer.compare(o1.i, o2.i);
					}
				}).addComparator(new Comparator<TestCollectionMatcher.B>() {
					
					@Override
					public int compare(B o1, B o2) {
						return o1.x.compareTo(o2.x);
					}
				})));
	}

	

}
