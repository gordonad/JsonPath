package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.PathTokenizer;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 1:22 PM
 */
public class PathTest {
    private static final Logger logger = LoggerFactory.getLogger(PathTest.class);

    Filter filter = new Filter() {
        @Override
        public boolean accept(Object obj) {
            return true;
        }

        @Override
        public Filter addCriteria(Criteria criteria) {
            return this;
        }
    };

    @Test
    public void path_is_not_definite() throws Exception {
        assertFalse(JsonPath.compile("$..book[0]").isPathDefinite());
        assertFalse(JsonPath.compile("$book[?]", filter).isPathDefinite());
        assertFalse(JsonPath.compile("$.books[*]").isPathDefinite());
    }

    @Test
    public void path_is_definite() throws Exception {
        assertTrue(JsonPath.compile("$.definite.this.is").isPathDefinite());
        assertTrue(JsonPath.compile("rows[0].id").isPathDefinite());
    }

    @Test
    public void valid_path_is_split_correctly() throws Exception {

        assertPath("$.store[*]", hasItems("$", "store", "[*]"));

        assertPath("$", hasItems("$"));

        assertPath("$..*", hasItems("$", "..", "*"));

        assertPath("$.store", hasItems("$", "store"));

        assertPath("$.store.*", hasItems("$", "store", "*"));

        assertPath("$.store[*].name", hasItems("$", "store", "[*]", "name"));

        assertPath("$..book[-1:].foo.bar", hasItems("$", "..", "book", "[-1:]", "foo", "bar"));

        assertPath("$..book[?(@.isbn)]", hasItems("$", "..", "book", "[?(@.isbn)]"));

        assertPath("['store'].['price']", hasItems("$", "store", "price"));

        assertPath("$.['store'].['price']", hasItems("$", "store", "price"));

        assertPath("$.['store']['price']", hasItems("$", "store", "price"));

        assertPath("$.['store'].price", hasItems("$", "store", "price"));

        assertPath("$.['store space']['price space']", hasItems("$", "store space", "price space"));

        assertPath("$.['store']['nice.price']", hasItems("$", "store", "nice.price"));

        assertPath("$..book[?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$.store.book[*].author", hasItems("$", "store", "book", "[*]", "author"));

        assertPath("$.store..price", hasItems("$", "store", "..", "price"));

    }

    @Test
    public void white_space_are_removed() throws Exception {

        assertPath("$.[ 'store' ]", hasItems("$", "store"));

        assertPath("$.[   'store' ]", hasItems("$", "store"));

        assertPath("$.['store bore']", hasItems("$", "store bore"));

        assertPath("$..book[  ?(@.price<10)  ]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(@.price<10  )]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[?(  @.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));

        assertPath("$..book[  ?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));
    }

    @Test
    public void dot_ending_ignored() throws Exception {

        assertPath("$..book['something'].", hasItems("$", "..", "something"));

    }

    @Test
    public void invalid_path_throws_exception() throws Exception {
        assertPathInvalid("$...*");
    }


    //----------------------------------------------------------------
    //
    // Helpers
    //
    //----------------------------------------------------------------

    private void assertPathInvalid(String path) {
        try {
            PathTokenizer tokenizer = new PathTokenizer(path);
            assertTrue("Expected exception!", false);
        } catch (InvalidPathException expected) {
        }
    }

    private void assertPath(String path, Matcher<Iterable<String>> matcher) {
        logger.debug("PATH: " + path);

        PathTokenizer tokenizer = new PathTokenizer(path);

        for (String fragment : tokenizer.getFragments()) {
            logger.debug(fragment);
        }

        assertThat(tokenizer.getFragments(), matcher);
        logger.debug("----------------------------------");
    }


}
