import { ShortenedURL } from '@/app/page';
import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node'

const urls: ShortenedURL[]  = [];

export const handlers = [
    http.get('http://localhost:8080/urls', () => HttpResponse.json(urls))
];


export const server = setupServer(...handlers);