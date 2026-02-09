import { expect, test, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from "@testing-library/user-event";
import Page from '../page'
import { server } from '@/mocks/server';
import { http, HttpResponse } from 'msw';
// By default return nothing

 
test('Page loads static content', () => {
    render(<Page />);
    expect(screen.getByText('Shorten a new URL')).toBeDefined();
    expect(screen.getByText('Shortened URLs')).toBeDefined();
});

test('Button is initially disabled', () => {
    render(<Page />);

    const button = screen.getByRole("button", { name: /Shorten/i });

    expect(button).toBeDisabled();
});

test('Button stays disabled until a URL is entered', async () => {
    render(<Page />);

    const button = screen.getByRole("button", { name: /Shorten/i });

    // Enter "http://"

    const urlInput = screen.getByRole("textbox", {name: /URL to shorten/});
    await userEvent.type(urlInput, "http://");
    expect(button).toBeDisabled();

    // Now enter "a"
    await userEvent.type(urlInput, "a");
    expect(button).toBeEnabled();
});

test('UI displays message when there are no URLs', () => {
    render(<Page />);

    const msg = screen.getByText("No urls have been shortened yet");

    expect(msg).toBeVisible();
});

test('UI displays URLs when they have been added', async () => {
  const mockUrls = [
    {
      alias: "google",
      fullUrl: "https://google.com",
      shortUrl: "http://sho.rt/google",
    },
    {
      alias: "example",
      fullUrl: "https://example.com",
      shortUrl: "http://sho.rt/example",
    },
  ];

  // Mock the API response for GET /urls
  server.use(
    http.get("http://localhost:8080/urls", () => {
      return HttpResponse.json(mockUrls);
    })
  );

  render(<Page />);

  // Wait for the URLs to appear
  for (const url of mockUrls) {
    const fullUrlCell = await screen.findByText(url.fullUrl);
    expect(fullUrlCell).toBeVisible();

    const shortUrlCell = await screen.findByText(url.shortUrl);
    expect(shortUrlCell).toBeVisible();
  }

  // Ensure the "No urls have been shortened yet" message is gone
  expect(screen.queryByText("No urls have been shortened yet")).toBeNull();
});

test('Button becomes disabled again when input is cleared', async () => {
    render(<Page />);

    const button = screen.getByRole("button", { name: /Shorten/i });
    const urlInput = screen.getByRole("textbox", { name: /URL to shorten/ });

    // Enter a valid URL
    await userEvent.type(urlInput, "http://example.com");
    expect(button).toBeEnabled();

    // Clear the input
    await userEvent.clear(urlInput);
    expect(button).toBeDisabled();
});

test('Displays error message for invalid URL', async () => {
    render(<Page />);

    const urlInput = screen.getByRole("textbox", { name: /URL to shorten/ });
    const aliasInput = screen.getByRole("textbox", { name: /alias/ });

    // Enter an invalid URL
    await userEvent.type(urlInput, "invalid-url");
    await userEvent.click(aliasInput);

    const errorMsg = await screen.findByText("Please enter a valid URL");
    expect(errorMsg).toBeVisible();
});

test('Form is cleared after successful request', async () => {
    const mockResponse = {
        alias: "testalias",
        fullUrl: "https://test.com",
        shortUrl: "http://sho.rt/testalias",
    };

    // Mock the API response for POST /shorten
    server.use(
        http.post("http://localhost:8080/shorten", () => {
            return HttpResponse.json(mockResponse);
        })
    );

    render(<Page />);

    const button = screen.getByRole("button", { name: /Shorten/i });
    const urlInput = screen.getByRole("textbox", { name: /URL to shorten/ });

    // Enter a valid URL and submit
    await userEvent.type(urlInput, "https://test.com");
    await userEvent.click(button);

    // URL input is cleared
    expect(urlInput).toHaveValue("");
});

test("Form does not allow for duplicated alias", async () => {
  const mockUrls = [
    {
      alias: "google",
      fullUrl: "https://google.com",
      shortUrl: "http://sho.rt/google",
    }
  ];

  // Mock the API response for GET /urls
  server.use(
    http.get("http://localhost:8080/urls", () => {
      return HttpResponse.json(mockUrls);
    })
  );

  render(<Page />);

  // Try re-using google
  const urlInput = screen.getByRole("textbox", { name: /URL to shorten/ });
  const aliasInput = screen.getByRole("textbox", { name: /alias/ });

  await userEvent.type(urlInput, "http://valid.com");
  await userEvent.type(aliasInput, "google");

  const errorMsg = await screen.findByText("This alias already exists");
  expect(errorMsg).toBeVisible();

  // check button is disabled
  const button = screen.getByRole("button", { name: /Shorten/i });
  expect(button).toBeDisabled();
});

test('Handles HTTP errors gracefully', async () => {
    server.use(
        http.get('http://localhost:8080/urls', () => {
            return HttpResponse.error();
        })
    );

    render(<Page />);

    const errorMsg = await screen.findByText('Failed to load URLs');
    expect(errorMsg).toBeVisible();
});

test('Copy button copies URL to clipboard', async () => {
    const mockUrls = [
        {
            alias: 'example',
            fullUrl: 'https://example.com',
            shortUrl: 'http://sho.rt/example',
        },
    ];

    server.use(
        http.get('http://localhost:8080/urls', () => {
            return HttpResponse.json(mockUrls);
        })
    );

    render(<Page />);

    const writeTextMock = vi.fn();
    
    Object.assign(navigator, {
        clipboard: {
        writeText: writeTextMock,
        }
    });


    const copyButton = await screen.findByRole('button', { name: /Copy/i });
    await userEvent.click(copyButton);

    // Check if the clipboard contains the short URL
    expect(writeTextMock).toHaveBeenCalledWith(mockUrls[0].shortUrl);

    // Check confirmation message sent
    const message = screen.getByText("URL copied")
    expect(message).toBeVisible();
});

test('Open in new tab button opens URL in a new tab', async () => {
    const mockUrls = [
        {
            alias: 'example',
            fullUrl: 'https://example.com',
            shortUrl: 'http://sho.rt/example',
        },
    ];

    server.use(
        http.get('http://localhost:8080/urls', () => {
            return HttpResponse.json(mockUrls);
        })
    );

    render(<Page />);

    const openButton = await screen.findByRole('button', { name: /Open in new tab/i });

    // Mock window.open
    const openSpy = vi.spyOn(window, 'open');

    await userEvent.click(openButton);

    expect(openSpy).toHaveBeenCalledWith(mockUrls[0].shortUrl, '_blank', "noopener,noreferrer");

    openSpy.mockRestore();
});

test('Delete button removes URL from the list', async () => {
    const mockUrls = [
        {
            alias: 'example',
            fullUrl: 'https://example.com',
            shortUrl: 'http://sho.rt/example',
        },
    ];

    server.use(
        http.get('http://localhost:8080/urls', () => {
            return HttpResponse.json(mockUrls);
        }),
        http.delete('http://localhost:8080/example', () => {
            // Remove from the list
            mockUrls.splice(0, 1);
            return new HttpResponse(null, { status: 204});
        })
    );

    render(<Page />);

    const deleteButton = await screen.findByRole('button', { name: /Remove alias/i });
    await userEvent.click(deleteButton);

    // Ensure the URL is removed from the list
    const msg = await screen.findByText("No urls have been shortened yet");
    expect(msg).toBeVisible();
});
