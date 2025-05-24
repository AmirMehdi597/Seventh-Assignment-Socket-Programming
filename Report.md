<h1>Three Ways to Send a Login Message</h1>
<h1>Question for method 1</h1>
<h2>Question 1</h2>
<h2>✅ Pros</h2>
<h4>Simplicity:

Easy to implement and understand.

No need for external libraries or complex parsing logic.

Lightweight:

Minimal overhead in both data size and CPU usage.

Well-suited for low-resource environments or simple applications.</h4>

<h2>❌ Cons</h2>
<h4>Lack of structure & type safety:

No enforcement of data types or structure.

You must manually parse and validate each part.

Brittle format:

Parsing can easily break if the format changes or if the delimiter (|) appears in the data itself (e.g., a username with a | character).

No extensibility:

Adding new fields or versioning becomes messy unless you design for it from the start.</h4>

<h2>Question 2</h2>
<h4> you can parse this using .split():<br>but if use | in command This breaks the logic — now the username and password are misaligned</h4>

<h2>Question 3</h2>
<h4> No, using delimited strings like "COMMAND|value1|value2|...|valueN" is not suitable for complex or nested data structures.<br>Why it's not suitable:<br><br>1- Flat structure only<br>2- Delimiters in data<br>3- Manual parsing grows exponentially harder</h4><br><br>
<h1>Questions for method 2</h1>
<h2>Question 1</h2>
<h2>✅ Advantages of sending a full Java object</h2>
<h3>1. Structured data with type safety<br>2. Built-in serialization<br>3. Code reusability</h3><br>
<h2>Question 2</h2>
<h3>Answer is No</h3>
<h3>🧱 Why?</h3>
<h4>Java's ObjectOutputStream and ObjectInputStream use Java-specific binary serialization. This format is:

Binary (not text)

Proprietary (specific to the Java Virtual Machine)

Requires that both sides (client and server) have the exact same class structure and serialVersionUID></h4><br>
<h1>Questions for method 3</h1>
<h2>Question 1</h2>
<h2>✅ Reasons why JSON is widely preferred:</h2>
<h3>1. ✅ Language-agnostic and universally supported
JSON is not tied to any single programming language.<br>

Almost every language — Java, Python, JavaScript, C#, Go, PHP, etc. — has built-in or easily available libraries to parse and generate JSON.

2.✅ Human-readable and easy to debug

3.✅ Supports complex and nested data</h3><br>
<h2>Question 2</h2>
<h3>The answer is Yes</h3>
<h2> Why JSON works across languages</h2><h3>1. Language-independent format
JSON is just plain text with a standardized structure (key-value pairs, arrays, etc.), not tied to any language’s internal data structures.<br><br>

2.Universal library support

3.Can be easily transmitted over the network</h3>