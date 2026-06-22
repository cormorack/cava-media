<html>
<body>
<h1>Connection Pool Test</h1>
<p>
    ${message}
</p>
<ul>
    <g:each in="${properties}" var="kvp">
        <li>${kvp.key} ==> ${kvp.value}</li>
    </g:each>
</ul>
</body>
</html>