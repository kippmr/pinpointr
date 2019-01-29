import React, { Component } from 'react';
import { withStyles } from '@material-ui/core/styles';
import { Map, TileLayer } from 'react-leaflet';
const stamenTonerTiles = 'http://stamen-tiles-{s}.a.ssl.fastly.net/toner-background/{z}/{x}/{y}.png';
const openStreetMapTiles = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
const stamenTonerAttr = 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a> &mdash; Map data &copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>';
const mapCenter = [39.9528, -75.1638];
const zoomLevel = 12;

const styles = {
    root: {
        width: '300px',
        height: '300px'
    }
};

function LoadLeaflet() {
        return (
            <div>
                <Map
                    center={mapCenter}
                    zoom={zoomLevel}
                >
                    <TileLayer
                        attribution={stamenTonerAttr}
                        url={stamenTonerTiles}
                    />
                </Map>
            </div>
        );
}

export default LoadLeaflet;