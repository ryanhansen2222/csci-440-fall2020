SELECT 1 + 1;

select 1 + 1;

SELECT TrackId,
       Name,
       Composer,
       UnitPrice
FROM tracks;

SELECT *
FROM tracks;

SELECT name
FROM tracks
WHERE Milliseconds > 3 * 60 * 1000;

SELECT name, AlbumId
FROM tracks
WHERE AlbumId = 1;

SELECT Title
FROM albums
WHERE   AlbumId = 1;

SELECT *
FROM artists
where art

SELECT *
FROM invoices
LIMIT 10
OFFSET 0;

SELECT *
FROM playlists
where playlists.

SELECT *
FROM employees
WHERE employees.
LIMIT 10
OFFSET 0;

SELECT *
FROM customers
LIMIT 10;

SELECT sum(invoices.Total), employees."FirstName", employees."LastName", inv
FROM invoices
join customers on customers.CustomerId = invoices.CustomerId
join employees on employees.EmployeeID = customers.SupportRepId
GROUP BY employees.EmployeeId;

SELECT employees.EmployeeID, employees.FirstName, employees.LastName, employees.Email, sum(invoices.Total) as SalesTotal, count(invoices.InvoiceId) as SalesCount FROM invoices
                             join customers on customers.CustomerId = invoices.CustomerId
                             join employees on employees.EmployeeID = customers.SupportRepId
                             GROUP BY employees.EmployeeId;

SELECT name, Milliseconds, AlbumId
FROM tracks
WHERE AlbumId = 1 OR Milliseconds > 3 * 60 * 1000;

SELECT name, Milliseconds, AlbumId
FROM tracks
WHERE NOT AlbumId = 1 AND Milliseconds > 3 * 60 * 1000;

select Name from tracks where Composer IS NULL;



