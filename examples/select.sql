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
FROM employees
WHERE EmployeeID=1;

UPDATE employees
SET FirstName="ANDREW", LastName="ADAMS"
WHERE EmployeeID=1;

select *
FROM albums
WHERE albums.

UPDATE artists
SEt Name = "GEORGE"
WHERE ArtistId = 1;

INSERT INTO artists
(Name) VALUES ("GEORGE MAN");

SELECT *
FROM invoices
LIMIT 10
OFFSET 0;

SELECT * From tracks Where tracks
;
SELECT *
FROM employees
WHERE

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

SELECT *
FROM playlist_track;

SELECT playlists.Name, tracks.Name
FROM playlists
join playlist_track on
playlists.PlaylistId=playlist_track.PlaylistId
join tracks ON playlist_track.TrackId=tracks.TrackId
WHERE playlists.PlaylistId=3
ORDER BY tracks.Name;
